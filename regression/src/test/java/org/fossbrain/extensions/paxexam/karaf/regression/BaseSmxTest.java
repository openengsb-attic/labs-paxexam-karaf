package org.fossbrain.extensions.paxexam.karaf.regression;

import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.options.CustomFrameworkOption;

/**
 * This one is deactivated till there is an SMX available with a newer karaf version
 */
// @RunWith(JUnit4TestRunner.class)
// @ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class BaseSmxTest {

    @Configuration
    public Option[] config() {
        return new Option[]{ new CustomFrameworkOption("mvn:org.apache.servicemix/apache-servicemix/4.3.0/zip",
            "servicemix",
            "servicemix-4.3.0") };
    }

    @Test
    public void test() throws Exception {
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
    }
}
