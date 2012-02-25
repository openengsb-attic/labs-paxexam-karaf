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

package org.openengsb.labs.paxexam.karaf.container.internal.examAdaptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openengsb.labs.paxexam.karaf.container.internal.Constants;
import org.openengsb.labs.paxexam.karaf.container.internal.KarafPropertiesFile;

public abstract class AbstractExamFeaturesFile implements ExamFeaturesFile {
    protected String featuresXml;

    @Override
    public void writeToFile(File featuresXmlFile) throws IOException {
        FileUtils.writeStringToFile(featuresXmlFile, featuresXml);
    }

    @Override
    public void adaptDistributionToStartExam(File karafHome, File featuresXmlFile) throws IOException {
        KarafPropertiesFile karafPropertiesFile = new KarafPropertiesFile(karafHome, Constants.FEATURES_CFG_LOCATION);
        karafPropertiesFile.load();
        String finalFilePath = ",file:" + featuresXmlFile.toString().replaceAll("\\\\", "/").replaceAll(" ", "%20");
        karafPropertiesFile.extend("featuresRepositories", finalFilePath);
        karafPropertiesFile.extend("featuresBoot", ",exam");
        karafPropertiesFile.store();
    }

    protected String getJunitVersion() {
        return getNamedVersion("junit.version");
    }

    protected String getInjectionVersion() {
        return getNamedVersion("injection.version");
    }

    protected String getOptionsVersion() {
        return getNamedVersion("options.version");
    }

    protected String getNamedVersion(String name) {
        String optionsVersion = "";
        try {
            final InputStream is = AbstractExamFeaturesFile.class.getClassLoader().getResourceAsStream(
                "META-INF/versions.properties"
                );
            if (is != null) {
                final Properties properties = new Properties();
                properties.load(is);
                optionsVersion = properties.getProperty(name, "").trim();
            }
        } catch (Exception ignore) {
            // use default versions
        }
        return optionsVersion;
    }
}
