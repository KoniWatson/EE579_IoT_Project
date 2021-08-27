package View;

import Model.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javax.swing.plaf.IconUIResource;
import java.util.ArrayList;

/**
 * The View is the "interface" for the GUI i.e. the elements that the user interacts with to set-up a recipe.
 */
public class View {
    TableView<MCx_Client> devicesTable;
    TableView<Client_MCx> settingTable;

    /**
     * This is the generation of the setting tables. Each column is given a name that correlates to Client_MCx class
     * that is used to make up the table. The size of these columns are aldo defined.
     * @param settingPane --> The pane that the table should be put on.
     * @return --> The created table.
     */
    public TableView<Client_MCx> generateSettingsTable(Pane settingPane) {
        settingTable = new TableView<>();
        settingTable.prefWidthProperty().bind(settingPane.widthProperty().multiply(1));
        settingTable.prefHeightProperty().bind(settingPane.heightProperty().multiply(1));

        TableColumn<Client_MCx, String> inputCol = new TableColumn<>("INPUT");
        inputCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        inputCol.prefWidthProperty().bind(settingPane.widthProperty().multiply(0.2));

        TableColumn<Client_MCx, String> outputCol = new TableColumn<>("OUTPUT");
        outputCol.setCellValueFactory(new PropertyValueFactory<>("output"));
        outputCol.prefWidthProperty().bind(settingPane.widthProperty().multiply(0.2));

        TableColumn<Client_MCx, String> eventCol = new TableColumn<>("EVENT");
        eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        eventCol.prefWidthProperty().bind(settingPane.widthProperty().multiply(0.2));

        TableColumn<Client_MCx, String> actionCol = new TableColumn<>("ACTION");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionCol.prefWidthProperty().bind(settingPane.widthProperty().multiply(0.2));

        TableColumn<Client_MCx, ProgressIndicator> progressCol = new TableColumn<>("PROGRESS");
        progressCol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        progressCol.prefWidthProperty().bind(settingPane.widthProperty().multiply(0.2));

        settingTable.getColumns().addAll(inputCol, actionCol, outputCol, eventCol, progressCol);

        return settingTable;
    }

    /**
     * This is the generation of the connected device table. Each column is given a name that correlates to MCx_Client class
     * that is used to make up the table. The size of these columns are aldo defined.
     * @param devicesPane --> The pane that the table should be put on.
     *      * @return --> The created table.
     */
    public TableView<MCx_Client> generateDevicesTable(Pane devicesPane) {
        devicesTable = new TableView<>();
        devicesTable.prefWidthProperty().bind(devicesPane.widthProperty().multiply(1));
        devicesTable.prefHeightProperty().bind(devicesPane.heightProperty().multiply(1));

        TableColumn<MCx_Client, String> nameCol = new TableColumn<>("NAME");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.prefWidthProperty().bind(devicesPane.widthProperty().multiply(0.25));

        TableColumn<MCx_Client, String> macCol = new TableColumn<>("MAC");
        macCol.setCellValueFactory(new PropertyValueFactory<>("MAC"));
        macCol.prefWidthProperty().bind(devicesPane.widthProperty().multiply(0.25));

        TableColumn<MCx_Client, String> configCol = new TableColumn<>("CONFIGURATION");
        configCol.setCellValueFactory(new PropertyValueFactory<>("config"));
        configCol.prefWidthProperty().bind(devicesPane.widthProperty().multiply(0.25));

        TableColumn<MCx_Client, String> statusCol = new TableColumn<>("STATUS");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.prefWidthProperty().bind(devicesPane.widthProperty().multiply(0.25));

        devicesTable.getColumns().addAll(nameCol,macCol,configCol,statusCol);

        return devicesTable;
    }

    /**
     * To allow for a new recipe to be added the setting must be added to the exiting item. This is what this
     * method does.
     * @param setting --> The new recipe to be added.
     */
    public void addNewSetting(Client_MCx setting) {
        settingTable.getItems().add(setting);
    }

    /**
     * To allow for a new connected device to be added the connected devices table must be added to the exiting items.
     * This is what this method does.
     * @param device --> The new device to be added.
     */
    public void addNewDevice(MCx_Client device) {
        devicesTable.getItems().add(device);
    }

    /**
     * To build a recipe the user must select a from several different choice box's, hence this method populates these
     * for the user.
     * @param microInput --> Input microcontroller
     * @param microOutput --> First output microcontroller
     * @param microEvent --> The first event to be performed
     * @param microAction --> The Action to be performed
     * @param microOutput2 --> Second output microcontroller
     * @param microEvent2 --> The second event to be occur after the action
     * @param colourSelect --> The advanced settings colour selection
     */
    public void populateChoiceBoxes(ChoiceBox<String> microInput, ChoiceBox<String> microOutput, ChoiceBox<String> microEvent, ChoiceBox<String> microAction, ChoiceBox<String> microOutput2, ChoiceBox<String> microEvent2, ChoiceBox<String> colourSelect){
        microAction.getItems().add("Choose a Action");
        microAction.getItems().add("Button");
        microAction.getItems().add("Switch");
        microAction.getItems().add("Potentiometer Value");
        microAction.getItems().add("Temperature Value");

        microEvent.getItems().add("Choose a Event");
        microEvent.getItems().add("RGB Cycle");
        microEvent.getItems().add("RGB Colour");
        microEvent.getItems().add(" Buzzer");
        microEvent.getItems().add("LED Green");
        microEvent.getItems().add("LED Red");
        microEvent.getItems().add(" Stop");

        microEvent2.getItems().add("Choose a Event");
        microEvent2.getItems().add("RGB Cycle");
        microEvent2.getItems().add("RGB Colour");
        microEvent2.getItems().add(" Buzzer");
        microEvent2.getItems().add("LED Green");
        microEvent2.getItems().add("LED Red");
        microEvent2.getItems().add(" Stop");

        colourSelect.getItems().add("Choose a Colour");
        colourSelect.getItems().add("Red");
        colourSelect.getItems().add("Green");
        colourSelect.getItems().add("Blue");
        colourSelect.getItems().add("Purple");
        colourSelect.getItems().add("White");

        microOutput.getItems().add("Choose a Device");
        microOutput2.getItems().add("Choose a Device");
        microInput.getItems().add("Choose a Device");

        resetRecipe(microInput, microOutput, microEvent,microAction,microOutput2, microEvent2,colourSelect);
    }

    /**
     * Certain Actions and Events require advanced setting. This method will display the respective advanced settings
     * depending on these Actions.
     * @param events --> Array that stores the events events[0] = first event, events[1] = second event
     * @param action --> Input action
     * @param advSet --> The advanced settings Pane
     * @param hold --> The button hold enter field pane
     * @param tempPot --> The potentiometer and temperature advanced settings pane
     */
    public void setActionAdvSettings(String[] events, String action, Pane advSet, Pane hold, Pane tempPot) {
        if (action.equals("Button")) {
            advSet.setVisible(true);
            hold.setVisible(true);
            tempPot.setVisible(false);
        }else if (action.equals("Temperature Value") || action.equals("Potentiometer Value")){
            advSet.setVisible(true);
            tempPot.setVisible(true);
            hold.setVisible(false);
        }else if (events[0].equals("RGB Colour") || events[1].equals("RGB Colour")){
            advSet.setVisible(true);
            hold.setVisible(false);
            tempPot.setVisible(false);
        }else {
            advSet.setVisible(false);
            hold.setVisible(false);
            tempPot.setVisible(false);
        }
    }

    /**
     * Certain Actions and Events require advanced setting. This method will display the respective advanced settings
     * depending on these Events.
     * @param events --> Array that stores the events events[0] = first event, events[1] = second event
     * @param action --> Input action
     * @param advSet --> The advanced settings Pane
     * @param colour --> The colour select pane
     * @param colour1 --> The colour cycle pane
     */
    public void setEventAdvSettings(String action, String[] events, Pane advSet, Pane colour, Pane colour1) {
        if (events[0].equals("RGB Colour") || events[1].equals("RGB Colour")) {
            advSet.setVisible(true);
            colour.setVisible(true);
            colour1.setVisible(false);
        }else if (events[0].equals("RGB Cycle") || events[1].equals("RGB Cycle")){
            advSet.setVisible(true);
            colour.setVisible(false);
            colour1.setVisible(true);
        }else if (action.equals("Button") || action.equals("Temperature Value") || action.equals("Potentiometer Value")){
            colour.setVisible(false);
            colour1.setVisible(false);
        }else {
            advSet.setVisible(false);
            colour.setVisible(false);
            colour1.setVisible(false);
        }
    }

    /**
     * Resetting the advanced settings.
     * @param advSet --> The advanced settings Pane
     * @param button --> Pane for the advanced setting for a button
     * @param hold --> The button hold enter field pane
     * @param tempPot --> The potentiometer and temperature advanced settings pane
     * @param colour --> The colour select pane
     * @param colour1 --> The colour cycle pane
     */
    public void resetAdvancedSettings(Pane advSet, Pane button, Pane hold, Pane tempPot, Pane colour, Pane colour1) {
        advSet.setVisible(false);
        button.setVisible(false);
        hold.setVisible(false);
        tempPot.setVisible(false);
        colour.setVisible(false);
        colour1.setVisible(false);
    }

    /**
     * Resetting the recipe dropdown options.
     * @param in--> Input microcontroller
     * @param out --> First output microcontroller
     * @param event --> The first event to be performed
     * @param action --> The Action to be performed
     * @param out2 --> Second output microcontroller
     * @param event2 --> The second event to be occur after the action
     * @param colourSelect --> The advanced settings colour selection
     */
    public void resetRecipe(ChoiceBox<String> in, ChoiceBox<String> out, ChoiceBox<String> event, ChoiceBox<String> action, ChoiceBox<String> out2, ChoiceBox<String> event2, ChoiceBox<String> colourSelect) {
        event.setValue("Choose a Event");
        action.setValue("Choose a Action");
        colourSelect.setValue("Choose a Colour");
        in.setValue("Choose a Device");
        out.setValue("Choose a Device");
        out2.setValue("Choose a Device");
        event2.setValue("Choose a Event");
    }

    /**
     * Adding new option to the input/output microcontroller dropdowns
     * @param microInput --> Input microcontroller
     * @param microOutput --> The first output microcontroller
     * @param microOutput2 --> The second output microcontroller
     * @param micro --> The name of the new microcontroller
     */
    public void addDevice(ChoiceBox<String> microInput, ChoiceBox<String> microOutput, ChoiceBox<String> microOutput2, String micro){
        microInput.getItems().add(micro);
        microOutput.getItems().add(micro);
        microOutput2.getItems().add(micro);
    }
}
