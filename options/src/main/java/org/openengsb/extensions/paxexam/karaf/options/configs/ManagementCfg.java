package org.openengsb.extensions.paxexam.karaf.options.configs;

import org.openengsb.extensions.paxexam.karaf.options.ConfigurationPointer;

/**
 * Pre configured property file pointers to the most commonly used properties in /etc/config.properties and
 * /etc/org.apache.karaf.management.cfg.
 */
public interface ManagementCfg {
    static final String FILE_PATH = "/etc/org.apache.karaf.management.cfg";

    /**
     * Port of the registry for the exported RMI service
     */
    static final ConfigurationPointer RMI_REGISTRY_PORT = new CustomPropertiesPointer("1099");

    /**
     * Port of the registry for the exported RMI service
     */
    static final ConfigurationPointer RMI_SERVER_PORT = new CustomPropertiesPointer("44444");

    static class CustomPropertiesPointer extends ConfigurationPointer {

        public CustomPropertiesPointer(String key) {
            super(FILE_PATH, key);
        }

    }
}
