package Model;

/**
 * Class constructed to handle the specific information of the connected microcontroller devices
 */
public class MCx_Client {
    private String config;
    private final String MAC, name, status;

    /**
     * Class constructor to set up the device specifications
     * @param MAC --> String of the new MAC
     * @param name --> Display name of the connected device
     */
    public MCx_Client(String MAC, String name) {
        this.MAC = MAC;
        this.name = "MC" + name;
        status = "Connected";
        config = "Input/Output";
    }

    /**
     * Method to retrieve the MAC address
     * @return --> The MAC address
     */
    public String getMAC() {
        return MAC;
    }

    /**
     * Method to get the display name of the microcontroller
     * @return --> Display name
     */
    public String getName() {
        return name;
    }

    /**
     * Method to obtain the configuration of the newly connected device
     * @return --> The config
     */
    public String getConfig() { return config; }

    /**
     * Mtehod to get the status of the device
     * @return --> The status
     */
    public String getStatus() { return status; }

    /**
     * Method to enable the config to be set
     * @param config --> String config
     */
    public void setConfig(String config) { this.config = config; }
}
