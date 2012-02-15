package org.openengsb.labs.paxexam.karaf.options;

import org.ops4j.pax.exam.Option;

/**
 * Option to configure the internal invoker for integration tests to be used.
 */
public class KarafExamSystemConfigurationOption implements Option {

    private String invoker;

    /**
     * define the pax.exam.invoker property as system property in the environment during creating the subproject.
     */
    public KarafExamSystemConfigurationOption(String invoker) {
        this.invoker = invoker;
    }

    public String getInvoker() {
        return invoker;
    }

}
