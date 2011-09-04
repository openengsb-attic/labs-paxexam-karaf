package org.fossbrain.extensions.paxexam.karaf.regression;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.CustomFrameworkOption;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class BaseOpenEngSBTest {

    @Configuration
    public Option[] config() {
        return new Option[]{ new CustomFrameworkOption("mvn:org.openengsb/openengsb/1.2.1.RELEASE/zip",
            "openengsb",
            "openengsb-1.2.1.RELEASE") };
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
