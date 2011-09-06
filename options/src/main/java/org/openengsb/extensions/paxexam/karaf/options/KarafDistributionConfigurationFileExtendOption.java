package org.openengsb.extensions.paxexam.karaf.options;

/**
 * This option allows to extend configurations in each configuration file based on the karaf.home location. The value
 * extends the current value (e.g. a=b to a=a,b) instead of replacing it. If there is no current value it is added.
 * 
 * If you would like to have add or replace functionality please use the
 * {@link KarafDistributionConfigurationFilePutOption} instead.
 */
public class KarafDistributionConfigurationFileExtendOption extends KarafDistributionConfigurationFileOption {

    public KarafDistributionConfigurationFileExtendOption(String configurationFilePath, String key, String value) {
        super(configurationFilePath, key, value);
    }

}
