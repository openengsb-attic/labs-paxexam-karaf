package org.openengsb.extensions.paxexam.karaf.options.configs;

import org.openengsb.extensions.paxexam.karaf.options.ConfigurationPointer;

/**
 * Pre configured property file pointers to the most commonly used properties in /etc/config.properties and
 * /etc/custom.properties.
 */
public interface CustomProperties {

    static final String FILE_PATH = "/etc/config.properties";

    /**
     * Possible values here are felix or equinox
     */
    static final ConfigurationPointer KARAF_FRAMEWORK = new CustomPropertiesPointer("karaf.framework");

    static final ConfigurationPointer SYSTEM_PACKAGES_EXTRA = new CustomPropertiesPointer(
        "org.osgi.framework.system.packages.extra");

    static final ConfigurationPointer BOOTDELEGATION = new CustomPropertiesPointer("org.osgi.framework.bootdelegation");

    static class CustomPropertiesPointer extends ConfigurationPointer {

        public CustomPropertiesPointer(String key) {
            super(FILE_PATH, key);
        }

    }
}
