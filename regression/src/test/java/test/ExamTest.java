package test;

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
public class ExamTest {

    @Configuration
    public Option[] config() {
        return new Option[]{ new CustomFrameworkOption("mvn:org.apache.karaf/apache-karaf/2.2.3/zip", "karaf",
            "karaf-2.2.3") };
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
