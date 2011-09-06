package org.openengsb.extensions.paxexam.karaf.options;

/**
 * This option allows to configure each configuration fille based on the karaf.home location. The value is "put". Which
 * means it is either replaced or added.
 * 
 * If you like to extend an option (e.g. make a=b to a=b,c) please make use of the
 * {@link KarafDistributionConfigurationFileExtendOption}.
 */
public class KarafDistributionConfigurationFilePutOption extends KarafDistributionConfigurationFileOption {

    public KarafDistributionConfigurationFilePutOption(String configurationFilePath, String key, String value) {
        super(configurationFilePath, key, value);
    }

}
