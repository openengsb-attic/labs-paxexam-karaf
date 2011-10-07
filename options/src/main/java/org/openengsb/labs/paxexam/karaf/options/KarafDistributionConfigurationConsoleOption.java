package org.openengsb.labs.paxexam.karaf.options;

import org.ops4j.pax.exam.Option;

/**
 * Option to configure the Karaf -Dkaraf.startLocalConsole and -Dkaraf.startRemoteShell options. Per default both are
 * started automatically. If you like to change this behavior simply add this option to your container configuration.
 */
public class KarafDistributionConfigurationConsoleOption implements Option {

    private Boolean startLocalConsole;
    private Boolean startRemoteShell;

    public KarafDistributionConfigurationConsoleOption(Boolean startLocalConsole, Boolean startRemoteShell) {
        this.startLocalConsole = startLocalConsole;
        this.startRemoteShell = startRemoteShell;
    }

    /**
     * Sets the -Dkaraf.startLocalConsole to true
     */
    public KarafDistributionConfigurationConsoleOption startLocalConsole() {
        startLocalConsole = true;
        return this;
    }

    /**
     * Sets the -Dkaraf.startLocalConsole to false
     */
    public KarafDistributionConfigurationConsoleOption ignoreLocalConsole() {
        startLocalConsole = false;
        return this;
    }

    /**
     * Sets the -Dkaraf.startRemoteShell to true
     */
    public KarafDistributionConfigurationConsoleOption startRemoteShell() {
        startRemoteShell = true;
        return this;
    }

    /**
     * Sets the -Dkaraf.startRemoteShell to false
     */
    public KarafDistributionConfigurationConsoleOption ignoreRemoteShell() {
        startRemoteShell = false;
        return this;
    }

    public Boolean getStartLocalConsole() {
        return startLocalConsole;
    }

    public Boolean getStartRemoteShell() {
        return startRemoteShell;
    }

}
