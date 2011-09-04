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

package org.fossbrain.extensions.paxexam.karaf.container.internal;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.ops4j.pax.exam.Info;

public class ExamFeaturesFile {

    private String featuresXml;

    public ExamFeaturesFile() {
        featuresXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<features name=\"pax-exam-features-"
                    + Info.getPaxExamVersion()
                    + "\">\n"
                    + "<feature name=\"exam\" version=\""
                    + Info.getPaxExamVersion()
                    + "\">\n"
                    + "<bundle>mvn:org.ops4j.pax.exam/pax-exam-util/"
                    + Info.getPaxExamVersion()
                    + "</bundle>\n"
                    + "<bundle>mvn:org.ops4j.pax.exam/pax-exam-extender-service/"
                    + Info.getPaxExamVersion()
                    + "</bundle>\n"
                    + "<bundle>mvn:org.ops4j.pax.exam/pax-exam-container-rbc/"
                    + Info.getPaxExamVersion()
                    + "</bundle>\n"
                    + "<bundle>mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.junit/4.7_3</bundle>\n"
                    + "<bundle>mvn:org.ops4j.pax.exam/pax-exam-invoker-junit/"
                    + Info.getPaxExamVersion()
                    +
                    "</bundle>\n"
                    + "<bundle>mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.javax-inject/1_1</bundle>\n"
                    + "<bundle>mvn:org.ops4j.pax.exam/pax-exam-inject/" + Info.getPaxExamVersion() + "</bundle>\n"
                    + "</feature>\n"
                    + "</features>";
    }

    public void writeToFile(File featuresXmlFile) throws IOException {
        FileUtils.writeStringToFile(featuresXmlFile, featuresXml);
    }

    public void adaptDistributionToStartExam(File karafHome, File featuresXmlFile) throws IOException {
        KarafPropertiesFile karafPropertiesFile = new KarafPropertiesFile(karafHome, Constants.FEATURES_CFG_LOCATION);
        karafPropertiesFile.load();
        karafPropertiesFile.extend("featuresRepositories", ",file:" + featuresXmlFile);
        karafPropertiesFile.extend("featuresBoot", ",exam");
        karafPropertiesFile.store();
    }
}
