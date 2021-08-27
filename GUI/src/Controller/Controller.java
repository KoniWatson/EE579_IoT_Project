package Controller;

import Model.Client_MCx;
import Model.MCx_Client;
import View.View;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Optional;

public class Controller implements MqttCallback {
    private MqttClient client;
    private final String logsTopic = "$SYS/broker/log/#";
    private final String broker = "tcp://thestarkeys.tech:1883";
    private String hostname;
    private final int qos = 0;

    private TableView setTable, devTable;
    private String action = "LED - ON";
    private String input;
    private int[] advSet = {0, 0};
    private final ArrayList<String> storedMACs = new ArrayList<>();
    private String[] events = {" ", " "};
    private String[] outputs = {" ", " "};

    public View view;
    private ArrayList<MCx_Client> devices;

    @FXML private ChoiceBox<String> microEvent1, microEvent2, microAction, microInput, microOutput1, microOutput2, colourSelect;
    @FXML private Pane thresholdValue, advancedSettings, button, buttonHold, tempPot, colour, colour1, conDev, recHis, newRec, device, his, addOutput, home;
    @FXML private CheckBox threshold, press, hold, step, cycle, con;
    @FXML private TextField thresholdVal, holdTime;

    /**
     ***************************************** MQTT CONNECTION HANDLING **********************************************
     */
    public Controller() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(addr);

            byte[] mac = networkInterface.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }

            hostname = sb.toString();
            hostname = hostname.replace('-', ':');
        } catch (UnknownHostException | SocketException ex) {
            System.out.println("Hostname can not be resolved");
        }

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            client = new MqttClient(broker, hostname, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            client.setCallback(this);

            System.out.println("Attempting broker connection to: " + broker);
            client.connect(connOpts);

            System.out.println("Connected");

            System.out.println("Subscribing: " + logsTopic);
            client.subscribe(logsTopic);

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
            System.out.println("Disconnected");
            client.close();
        }catch (MqttException me) {
            System.err.println(me.getMessage());
        }
    }

    public void sendMessage(String content, String pubTopic) {
        try {
            MqttMessage message = new MqttMessage(content.getBytes());
            System.out.println("Publishing on topic: " + pubTopic + " |||| Payload: " + message);
            message.setQos(qos);
            client.publish(pubTopic, message);
        } catch (MqttException me) {
            System.out.println(me.getMessage());
        }
    }

    public void deviceSignUp() {
        try {
            client.subscribe("signup");
        }catch (MqttException me) {
            System.err.println(me.getMessage());
        }
    }

    public void clientSignUp(String MMAC) {
        try {
            MqttMessage message = new MqttMessage(hostname.getBytes());
            client.publish(MMAC + "/signup", message);
        }catch (MqttException me) {
            System.out.println(me.getMessage());
        }
    }

    public void unsubTopic(String topic) {
        try {
            client.unsubscribe(topic);
        }catch (MqttException me) {
            System.out.println(me.getMessage());
        }
    }

    public String getHostname() { return hostname; }

    @Override
    public void connectionLost(Throwable throwable) {
        System.err.println("Lost connection to broker");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        String message = mqttMessage.toString();

        System.out.println("------------------------------------");
        System.out.println("Received Message on: " + topic);
        System.out.println(message);
        System.out.println("------------------------------------");

        if(topic.equals("signup")) {
            if (!storedMACs.contains(message)) {
                devices.add(new MCx_Client(message, String.valueOf(storedMACs.size())));
                addDevice();
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) { }
    /*
     ****************************************************************************************************************
     */

    /**
     **************************************** APPLICATION INITIALIZE + CLOSING *******************************
     */

    /**
     * Listener to check if the application has been closed and to act according if it has i.e. disconnect from the
     * MQTT broker.
     * @param stage
     */
    public void setOnClose(Stage stage) {
        stage.setOnCloseRequest(close -> {
            view.resetAdvancedSettings(advancedSettings, button, buttonHold, tempPot, colour, colour1);
            disconnect();
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Initializer for the view. This method sets up all the tables, checkboxes and event listeners for everything in
     * the view.
     */
    @FXML
    public void initialize() {
        view = new View();
        devices = new ArrayList<>();

        view.populateChoiceBoxes(microInput, microOutput1, microEvent1, microAction, microOutput2, microEvent2, colourSelect);
        setTable = view.generateSettingsTable(recHis);
        devTable = view.generateDevicesTable(device);
        recHis.getChildren().add(setTable);
        device.getChildren().add(devTable);

        microEvent1.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            events[0] = microEvent1.getItems().get((int) number2);
            view.setEventAdvSettings(action,events,advancedSettings,colour,colour1);
        });
        microEvent2.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            events[1] = microEvent2.getItems().get((int) number2);
            view.setEventAdvSettings(action,events,advancedSettings,colour,colour1);
        });

        microAction.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            action = microAction.getItems().get((int) number2);
            view.setActionAdvSettings(events,action,advancedSettings,button, tempPot);
        });

        microInput.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            input = microInput.getItems().get((int) number2);
        });

        microOutput1.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            outputs[0] = microOutput1.getItems().get((int) number2);
        });
        microOutput2.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            outputs[1] = microOutput2.getItems().get((int) number2);
        });

        colourSelect.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            advSet[1] = (int) number2 - 1;
        });

        holdTime.textProperty().addListener((observable, oldValue, newValue) -> {
            advSet[0] = Integer.parseInt(newValue);
        });

        thresholdVal.textProperty().addListener((observable, oldValue, newValue) -> {
            advSet[0] = Integer.parseInt(newValue);
        });

        TableView.TableViewSelectionModel<Client_MCx> selectionModel = setTable.getSelectionModel();
        ObservableList<Client_MCx> selectedItems = selectionModel.getSelectedItems();
        selectedItems.addListener(new ListChangeListener<Client_MCx>() {
            @Override
            public void onChanged(Change<? extends Client_MCx> change) {
                Alert reRun = new Alert(Alert.AlertType.CONFIRMATION);
                reRun.setTitle("Re-Run");
                reRun.setHeaderText("Re-Run the selected recipe?");
                reRun.setContentText("Select OK if you wish to re-run the selected recipe.");
                Optional<ButtonType> result = reRun.showAndWait();

                if (result.get() == ButtonType.OK) {
                    input = change.getList().get(0).getName();
                    outputs = change.getList().get(0).getOutputs();
                    events = change.getList().get(0).getEvents();
                    action = change.getList().get(0).getAction();
                    advSet = change.getList().get(0).getAdvSet();
                    sendSettings();
                }
            }
        });
    }
    /*
     **********************************************************************************************************
     */

    /**
     * This will perform the relative checks to ensure a recipe is constructed correctly and make the right method
     * calls needed to construct and send the messages to the MQTT broker topic(s)
     */
    @FXML
    public void sendSettings() {
        String in = null, out0 = null, out1 = null, inHist = null;

        for (MCx_Client c: devices) {
            if(c.getName().equals(input)) {
                in = c.getMAC();
                inHist = c.getName();
            }
            if(c.getName().equals(outputs[0]))
                out0 = c.getMAC();
            if (!(events[1].equals(" ") && outputs[1].equals(" ")))
                if(c.getName().equals(outputs[1]))
                    out1 = c.getMAC();
            if(in != null && out0 != null && out1 != null)
                break;
        }

        if(in == null || out0 == null || inHist == null) {
            Alert missing = new Alert(Alert.AlertType.ERROR);
            missing.setTitle("Missing Information");
            missing.setHeaderText("ERROR - Recipe Incomplete");
            missing.setContentText("Please select an option for each dropdown");
            missing.showAndWait();
        }else {

            Client_MCx connectionInfo = new Client_MCx(in, inHist, outputs.clone(), events.clone(), action, advSet.clone());
            view.addNewSetting(connectionInfo);

            connectionInfo.setOutputMessage(events[0]);
            sendMessage(connectionInfo.getInputMessage(), getHostname() + "/" + in);
            sendMessage(connectionInfo.getOutputMessage(), getHostname() + "/" + out0);

            if (!(events[1].equals(" ") && outputs[1].equals(" "))) {
                connectionInfo.setOutputMessage(events[1]);
                sendMessage(connectionInfo.getOutputMessage(), getHostname() + "/" + out1);
            }

            view.resetAdvancedSettings(advancedSettings, button, buttonHold, tempPot, colour, colour1);
            view.resetRecipe(microInput, microOutput1, microEvent1, microAction, microOutput2, microEvent2, colourSelect);
        }
    }

    /**
     ***************************************** NAVIGATION BAR ****************************************************
     */

    /**
     * User interaction with the connected devices button callback. It will hide and show the correct panes for that
     * button click
     */
    @FXML
    void connectedDevicesPane() {
        newRec.setVisible(false);
        his.setVisible(false);
        conDev.setVisible(true);
        home.setVisible(false);
    }

    /**
     * User interaction with the new recipe button callback. It will hide and show the correct panes for that
     * button click
     */
    @FXML
    void newRecipePane() {
        newRec.setVisible(true);
        his.setVisible(false);
        conDev.setVisible(false);
        home.setVisible(false);
    }

    /**
     * User interaction with the recipe history button callback. It will hide and show the correct panes for that
     * button click
     */
    @FXML
    void recipeHisPane() {
        newRec.setVisible(false);
        his.setVisible(true);
        conDev.setVisible(false);
        home.setVisible(false);
    }

    /*
     **********************************************************************************************************
     */

    /**
     ********************************************* CONNECTED DEVICES *********************************************
     */

    /**
     * User interaction with the connect new device button callback. It will tell the application to listen for a new
     * device.
     */
    @FXML
    void newDevice() {
        deviceSignUp();
    }

    /**
     * Method that receives the information about the newly connected device and passes it to the right place.
     */
    void addDevice() {
        unsubTopic("signup");
        clientSignUp(devices.get(devices.size()-1).getMAC());
        storedMACs.add(devices.get(devices.size()-1).getMAC());
        view.addNewDevice(devices.get(devices.size()-1));
        view.addDevice(microInput, microOutput1, microOutput2, devices.get(devices.size()-1).getName());
    }
    /*
     ************************************************************************************************************
     */

    /**
     ****************************************** ADDITIONAL SETTINGS *********************************************
     */

    /**
     * User interaction with the add more outputs button callback. It will hide and show the correct panes for that
     * button click
     */
    @FXML
    void additionalOutputs() {
        addOutput.setVisible(true);
    }

    /**
     * User interaction with the x button callback. It will hide and show the correct panes for that button click
     */
    @FXML
    void closeAddOutput() {
        addOutput.setVisible(false);
        events[1] = " ";
        outputs[1] = " ";
    }

    /**
     * This method will hide setting options not required for that given advanced setting and adjust any advset values
     */
    @FXML
    void thresholdSetting() {
        con.setSelected(false);
        thresholdValue.setVisible(threshold.isSelected());
    }

    @FXML
    void buttonPress() {
        hold.setSelected(false);
        buttonHold.setVisible(false);
        advSet[0] = 0;
    }

    @FXML
    void buttonHold() {
        buttonHold.setVisible(true);
        press.setSelected(false);
    }

    @FXML
    void conCycle() {
        step.setSelected(false);
        advSet[1] = 6;
    }

    @FXML
    void colourStep() {
        cycle.setSelected(false);
        advSet[1] = 0;
    }

    @FXML
    void conPolling() {
        thresholdValue.setVisible(false);
        threshold.setSelected(false);
        advSet[0] = 0;
    }
    /*
     ***************************************************************************************************************
     */
}
