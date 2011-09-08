package org.openengsb.labs.paxexam.karaf.options.configs;

import org.openengsb.labs.paxexam.karaf.options.ConfigurationPointer;

/**
 * Pre configured property file pointers to the most commonly used properties in /etc/config.properties and
 * /etc/jre.properties.
 */
public interface JreProperties {

    static final String FILE_PATH = "/etc/jre.properties";

    static final ConfigurationPointer JRE15 = new CustomPropertiesPointer("jre-1.5");
    static final ConfigurationPointer JRE16 = new CustomPropertiesPointer("jre-1.6");
    static final ConfigurationPointer JRE17 = new CustomPropertiesPointer("jre-1.7");

    static class CustomPropertiesPointer extends ConfigurationPointer {

        public CustomPropertiesPointer(String key) {
            super(FILE_PATH, key);
        }

    }
}
