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

}
