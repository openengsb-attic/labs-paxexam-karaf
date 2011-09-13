package org.openengsb.labs.paxexam.karaf.options;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.MavenUrlReference;

/**
 * Option describing the karaf distribution to use. Without this option no karaf based distribution can be used in exam
 * tests.
 */
public class KarafDistributionConfigurationOption implements Option {

    private String frameworkURL;
    private MavenUrlReference frameworkURLReference;
    private String name;
    private String karafVersion;

    public KarafDistributionConfigurationOption() {
        frameworkURL = null;
        frameworkURLReference = null;
        name = null;
        karafVersion = null;
    }

    public KarafDistributionConfigurationOption(String frameworkURL, String name, String karafVersion) {
        this.frameworkURL = frameworkURL;
        frameworkURLReference = null;
        this.name = name;
        this.karafVersion = karafVersion;
    }

    public KarafDistributionConfigurationOption(MavenUrlReference frameworkURLReference, String name,
            String karafVersion) {
        frameworkURL = null;
        this.frameworkURLReference = frameworkURLReference;
        this.name = name;
        this.karafVersion = karafVersion;
    }

    public KarafDistributionConfigurationOption frameworkUrl(String frameworkURL) {
        this.frameworkURL = frameworkURL;
        return this;
    }

    public KarafDistributionConfigurationOption frameworkUrl(MavenUrlReference frameworkURL) {
        frameworkURLReference = frameworkURL;
        return this;
    }

    public KarafDistributionConfigurationOption name(String name) {
        this.name = name;
        return this;
    }

    public KarafDistributionConfigurationOption karafVersion(String karafVersion) {
        this.karafVersion = karafVersion;
        return this;
    }

    public String getFrameworkURL() {
        if (frameworkURL == null && frameworkURLReference == null) {
            throw new IllegalStateException("Either frameworkurl or frameworkUrlReference need to be set.");
        }
        return frameworkURL != null ? frameworkURL : frameworkURLReference.getURL();
    }

    public String getName() {
        return name;
    }

    public String getKarafVersion() {
        return karafVersion;
    }

}
