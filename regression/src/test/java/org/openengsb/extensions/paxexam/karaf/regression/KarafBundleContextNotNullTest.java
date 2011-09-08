package org.openengsb.extensions.paxexam.karaf.regression;

import static org.junit.Assert.assertNotNull;
import static org.openengsb.extensions.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;

import javax.inject.Inject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class KarafBundleContextNotNullTest {

    @Inject
    private BundleContext bundleContext;

    @BeforeClass
    public void beforeClass() {
        System.out.println("=================================");
        System.out.println("CLASS Before bundleContext validation CLASS");
        System.out.println("=================================");
    }

    @Before
    public void setUp() {
        System.out.println("=================================");
        System.out.println("Before bundleContext validation");
        System.out.println("=================================");
    }

    @Configuration
    public Option[] config() {
        return new Option[]{ karafDistributionConfiguration("mvn:org.apache.karaf/apache-karaf/2.2.3/zip", "karaf",
            "2.2.3") };
    }

    @Test
    public void test() throws Exception {
        assertNotNull(bundleContext);
    }

    @After
    public void tearDown() {
        System.out.println("=================================");
        System.out.println("After  bundleContext validation");
        System.out.println("=================================");
    }

    @AfterClass
    public void afterClass() {
        System.out.println("=================================");
        System.out.println("CLASS After bundleContext validation CLASS");
        System.out.println("=================================");
    }

}
