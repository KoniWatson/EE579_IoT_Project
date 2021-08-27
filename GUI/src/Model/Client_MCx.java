package Model;

import javafx.scene.control.ProgressIndicator;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Class to handle, build and manipulate the messages
 */
public class Client_MCx {
    private String inputMessage, outputMessage, input, action, topic, name;
    private int mode, thresholdIn, thresholdOut;
    private ProgressIndicator progress;
    private String[] events, outputs;
    private String[] in1;

    public Client_MCx(String input, String name, String[] outputs, String[] events, String action, int[] threshold) {
        this.input = input;
        this.outputs = outputs;
        this.action = action;
        this.name = name;
        this.events = events;
        thresholdIn = threshold[0];
        thresholdOut = threshold[1];

        progress = new ProgressIndicator();
        progress.setProgress(0.5);

        in1 = action.split(" ");

        setInputMessage();
        setMode();
        setTopic();
    }

    public void setMode() {
        if (action.equals("Button") || action.equals("Switch")) {
            mode = 0;
        }else {
            mode = 1;
        }
    }
   
    public void setInputMessage() {
        if (in1[0].equals("Potentiometer")) {
            in1[0] = "Potent";
            mode = 1;
        }else if (in1[0].equals("Temperature")){
            in1[0] = "Temper";
            mode = 1;
        }
    	inputMessage = in1[0].toUpperCase(Locale.ROOT) + "," + "," + thresholdIn + "," + mode;
    } 

    public void setOutputMessage(String event) {
        String[] temp = event.split(" ");
        event = temp[1] + temp[0];

        if (event.equals("CycleRGB") && thresholdOut == 6) {
            mode = 1;
            thresholdOut = 0;
        }

    	outputMessage = event.toUpperCase(Locale.ROOT) + ","+ topic + "," + thresholdOut + "," + mode;
    }

    public void setTopic() {
        topic = input + "/" + in1[0].toUpperCase(Locale.ROOT);
    }

    public String getName() { return name;}

    public String getInputMessage() {
        return inputMessage;
    }

    public String getOutputMessage() {
        return outputMessage;
    }

    public String getTopic() {
        return topic;
    }

    public String getInput(){
        return input;
    }

    public String getOutput() {
        String returner;

        if(outputs[1].equals(" "))
            returner = outputs[0];
        else
            returner = outputs[0] + "\n" + outputs[1];

        return returner;
    }

    public String getEvent() {
        if(events[1].equals(" "))
            return events[0];
        else
            return events[0] + "\n" + events[1];
    }

    public String getAction() {
        return action;
    }

    public ProgressIndicator getProgress() {
        return progress;
    }

    public void updateProgress() {
        progress.setProgress(1);
    }

    public String[] getOutputs() { return outputs; }
    public String[] getEvents() { return events; }
    public int[] getAdvSet() {
        int[] advance = {0,0};
        advance[0] = thresholdIn;
        advance[1] = thresholdOut;
        return advance;
    }
}
