#include "EspMQTTClient.h"

// Fill in these two fields in the secret.ino file
const extern char* ssid;
const extern char* pswd;

char incomingBuffer[500];   //Buffer to read incoming serial data from MSP
const char* mqttServer = "thestarkeys.tech";
const int mqttPort = 1883;
const char* mqttUser = "ChooseYourOwnName";
String mac;

EspMQTTClient client(
  ssid,         // Wifi SSID
  pswd,         // Wifi password
  mqttServer,   // MQTT Broker server ip
  mqttUser,     // Client name that uniquely identify your device
  mqttPort      // The MQTT port, default to 1883. this line can be omitted
);


// Callback for when receiving an MQTT message on a subscribed topic
void callback(const String &topic, const String &payload) {
  if(topic == mac + "/signup") {  // If it is on the dedicated setup topic, subscribe to the topic that desktop app will send instructions to
    client.subscribe(payload + "/" + mac, callback);
  } else {  // Else pass the topic and payload onto MSP and that will deal with it
    Serial.print(topic + "," + payload + '\0');
  }
}

void setup()
{
  Serial.begin(9600);

  // Optional functionnalities of EspMQTTClient : 
  client.enableDebuggingMessages(false); // Disable MQTT debugging messages sent to serial output
  client.enableLastWillMessage("sameAsMqttUserNameHere/lastwill", "It's been a long day without you, my friend, and I'll tell you all about it when I see you again");  // Set MQTT last will message
}

// This function is called once everything is connected (Wifi and MQTT)
// It gets the MAC address of the esp and subscribes to a topic to
// receive setup instructions from the desktop app.
// Then publishes its MAC to a topic so that desktop app knows its alive
void onConnectionEstablished()
{
  mac = WiFi.macAddress();
  client.subscribe(mac + "/signup", callback);
  client.publish("signup", mac);
}

// The main loop deals entirely with what the MSP sends to the ESP
void loop()
{
  // Messages will be in a CSV foramt, this method parses those messages straight from the input buffer
  // format will be pub/sub,topic,payload+\0
  // See design document for more information
  if(Serial.available() > 0) {  

    //read pub/sub topic and turn it into an int
    int charsIn = Serial.readBytesUntil(',', incomingBuffer, 500);
    int pub = atoi(incomingBuffer);
    memset(incomingBuffer, 0, charsIn);

    //read the topic into a String
    charsIn = Serial.readBytesUntil(',', incomingBuffer, 500);
    String topic = String(incomingBuffer);
    memset(incomingBuffer, 0, charsIn);

    //read the payload into a String
    charsIn = Serial.readBytesUntil('\0', incomingBuffer, 500);
    String payload = String(incomingBuffer);
    memset(incomingBuffer, 0, charsIn);
    
    if(topic == "mac") { // check if msp wants the mac address
      Serial.print(mac + '\0');
    }else { //if not mac address then it's a pub or sub request
      if(pub) {
        // Publish to desired topic
        client.publish(topic, payload);
      }else {
        // Subscribe to desired topic
        client.subscribe(topic, callback);
      }
    }
    
  }

  client.loop();  // Handles WiFi and MQTT connection
}
