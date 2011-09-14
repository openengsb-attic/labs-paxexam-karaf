package org.openengsb.labs.paxexam.karaf.regression;

import static junit.framework.Assert.assertTrue;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.maven;

import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;

/**
 * This test should validate if the versionAsInProject tags work as expected.
 */
// @RunWith(JUnit4TestRunner.class)
// @ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class VersionAsInProjectServicemixTest {

    @Configuration
    public Option[] config() {
        return new Option[]{ karafDistributionConfiguration().frameworkUrl(
            maven().groupId("org.apache.servicemix").artifactId("apache-servicemix").type("zip").versionAsInProject()) };
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
        assertTrue(true);
    }

}
