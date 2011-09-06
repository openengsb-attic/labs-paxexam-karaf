package org.openengsb.extensions.paxexam.karaf.options;

import org.ops4j.pax.exam.Option;

public final class KarafDistributionOption {

    public static Option karafDistributionConfiguration() {
        return new KarafDistributionConfigurationOption();
    }

}
