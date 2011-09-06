package org.openengsb.extensions.paxexam.karaf.options;

import org.ops4j.pax.exam.Option;

/**
 * Option describing the karaf distribution to use. Without this option no karaf based distribution can be used in exam
 * tests.
 */
public class KarafDistributionConfigurationOption implements Option {

    private final String frameworkURL;
    private final String name;
    private final String karafVersion;

    public KarafDistributionConfigurationOption(String frameworkURL, String name, String karafVersion) {
        this.frameworkURL = frameworkURL;
        this.name = name;
        this.karafVersion = karafVersion;
    }

    public String getFrameworkURL() {
        return frameworkURL;
    }

    public String getName() {
        return name;
    }

    public String getKarafVersion() {
        return karafVersion;
    }

}
