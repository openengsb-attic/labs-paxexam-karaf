package org.openengsb.extensions.paxexam.karaf.options;

import org.ops4j.pax.exam.Option;

/**
 * Abstract configuration file option. This one should not used directly but rather via
 * {@link KarafDistributionConfigurationFileExtendOption} or {@link KarafDistributionConfigurationFilePutOption}.
 */
public abstract class KarafDistributionConfigurationFileOption implements Option {

    private String configurationFilePath;
    private String key;
    private String value;

    public KarafDistributionConfigurationFileOption(ConfigurationPointer pointer, String value) {
        this(pointer.getConfigurationFilePath(), pointer.getKey(), value);
    }

    public KarafDistributionConfigurationFileOption(String configurationFilePath, String key, String value) {
        this.configurationFilePath = configurationFilePath;
        this.key = key;
        this.value = value;
    }


    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
