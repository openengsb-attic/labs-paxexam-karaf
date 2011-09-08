package org.openengsb.labs.paxexam.karaf.options.configs;

import org.openengsb.labs.paxexam.karaf.options.ConfigurationPointer;

/**
 * Pre configured property file pointers to the most commonly used properties in /etc/config.properties and
 * /etc/org.apache.karaf.features.cfg.
 */
public interface FeaturesCfg {
    static final String FILE_PATH = "/etc/org.apache.karaf.features.cfg";

    static final ConfigurationPointer REPOSITORIES = new CustomPropertiesPointer("featuresRepositories");
    static final ConfigurationPointer BOOT = new CustomPropertiesPointer("featuresBoot");

    static class CustomPropertiesPointer extends ConfigurationPointer {

        public CustomPropertiesPointer(String key) {
            super(FILE_PATH, key);
        }

    }
}
