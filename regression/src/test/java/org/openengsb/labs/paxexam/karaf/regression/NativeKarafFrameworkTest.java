/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.labs.paxexam.karaf.regression;

import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openengsb.labs.paxexam.karaf.options.KarafDistributionKitConfigurationOption;
import org.openengsb.labs.paxexam.karaf.options.KarafDistributionKitConfigurationOption.Platform;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class NativeKarafFrameworkTest {

    @Configuration
    public Option[] config() {
        return new Option[]{
            new KarafDistributionKitConfigurationOption("mvn:org.apache.karaf/apache-karaf/2.2.4/zip", "karaf",
                "2.2.4", Platform.WINDOWS).executable("bin\\karaf.bat").filesToMakeExecutable("bin\\admin.bat"),
            new KarafDistributionKitConfigurationOption("mvn:org.apache.karaf/apache-karaf/2.2.4/tar.gz", "karaf",
                "2.2.4", Platform.NIX).executable("bin/karaf").filesToMakeExecutable("bin/admin") };
    }

    @Test
    public void test() throws Exception {
        assertTrue(true);
    }

}
