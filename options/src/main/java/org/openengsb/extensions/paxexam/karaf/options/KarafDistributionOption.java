package org.openengsb.extensions.paxexam.karaf.options;

import org.ops4j.pax.exam.Option;

/**
 * Final class to provide an easy and intuitive way to configure the specific karaf distribution options.
 */
public final class KarafDistributionOption {

    /**
     * Configures which distribution options to use. Relevant are the frameworkURL, the frameworkName and the Karaf
     * version since all of those params are relevant to decide which internal configurations to use.
     */
    public static Option karafDistributionConfiguration(String frameworkURL, String name, String karafVersion) {
        return new KarafDistributionConfigurationOption(frameworkURL, name, karafVersion);
    }

    public static Option editConfigurationFilePut(String configurationFilePath, String key, String value) {
        return new KarafDistributionConfigurationFilePutOption(configurationFilePath, key, value);
    }

    public static Option editConfigurationFilePut(ConfigurationPointer configurationPointer, String value) {
        return new KarafDistributionConfigurationFilePutOption(configurationPointer, value);
    }

    public static Option editConfigurationFileExtend(String configurationFilePath, String key, String value) {
        return new KarafDistributionConfigurationFileExtendOption(configurationFilePath, key, value);
    }

    public static Option editConfigurationFileExtend(ConfigurationPointer configurationPointer, String value) {
        return new KarafDistributionConfigurationFileExtendOption(configurationPointer, value);
    }

}
