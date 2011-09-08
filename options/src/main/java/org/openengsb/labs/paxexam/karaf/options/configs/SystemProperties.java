package org.openengsb.labs.paxexam.karaf.options.configs;

import org.openengsb.labs.paxexam.karaf.options.ConfigurationPointer;

/**
 * Pre configured property file pointers to the most commonly used properties in /etc/config.properties and
 * /etc/system.properties..
 */
public interface SystemProperties {
    static final String FILE_PATH = "/etc/system.properties";

    static final ConfigurationPointer KARAF_NAME = new CustomPropertiesPointer("karaf.name");

    static class CustomPropertiesPointer extends ConfigurationPointer {

        public CustomPropertiesPointer(String key) {
            super(FILE_PATH, key);
        }

    }
}
