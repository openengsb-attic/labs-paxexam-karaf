package org.openengsb.extensions.paxexam.karaf.options.configs;

import org.openengsb.extensions.paxexam.karaf.options.ConfigurationPointer;

/**
 * Pre configured property file pointers to the most commonly used properties in /etc/config.properties and
 * /etc/org.ops4j.pax.web.cfg.
 */
public class WebCfg {
    static final String FILE_PATH = "/etc/org.ops4j.pax.web.cfg";

    static final ConfigurationPointer HTTP_PORT = new CustomPropertiesPointer("org.osgi.service.http.port");

    static class CustomPropertiesPointer extends ConfigurationPointer {

        public CustomPropertiesPointer(String key) {
            super(FILE_PATH, key);
        }

    }
}
