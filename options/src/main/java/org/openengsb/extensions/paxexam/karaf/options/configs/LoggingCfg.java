package org.openengsb.extensions.paxexam.karaf.options.configs;

import org.openengsb.extensions.paxexam.karaf.options.ConfigurationPointer;

/**
 * Pre configured property file pointers to the most commonly used properties in /etc/config.properties and
 * /etc/org.ops4j.pax.logging.cfg.
 */
public interface LoggingCfg {
    static final String FILE_PATH = "/etc/org.ops4j.pax.logging.cfg";

    static final ConfigurationPointer ROOT_LOGGER = new CustomPropertiesPointer("log4j.rootLogger");

    static class CustomPropertiesPointer extends ConfigurationPointer {

        public CustomPropertiesPointer(String key) {
            super(FILE_PATH, key);
        }

    }
}
