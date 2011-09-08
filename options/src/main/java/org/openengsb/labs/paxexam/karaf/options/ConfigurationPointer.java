package org.openengsb.labs.paxexam.karaf.options;

/**
 * Bundled configuration for making it easily static accessible via classes.
 */
public class ConfigurationPointer {

    private String configurationFilePath;
    private String key;

    public ConfigurationPointer(String configurationFilePath, String key) {
        this.configurationFilePath = configurationFilePath;
        this.key = key;
    }

    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    public String getKey() {
        return key;
    }

}
