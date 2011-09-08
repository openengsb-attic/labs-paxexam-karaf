package org.openengsb.labs.paxexam.karaf.options.configs;

import org.openengsb.labs.paxexam.karaf.options.ConfigurationPointer;

/**
 * Pre configured property file pointers to the most commonly used properties in /etc/config.properties and
 * /etc/users.properties.
 */
public interface UsersProperties {
    static final String FILE_PATH = "/etc/users.properties";

    static final ConfigurationPointer KARAF_USER = new CustomPropertiesPointer("karaf");

    static class CustomPropertiesPointer extends ConfigurationPointer {

        public CustomPropertiesPointer(String key) {
            super(FILE_PATH, key);
        }

    }
}
