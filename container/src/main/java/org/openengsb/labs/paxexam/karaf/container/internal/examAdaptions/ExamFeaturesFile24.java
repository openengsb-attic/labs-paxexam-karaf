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

import org.ops4j.pax.exam.Info;

/**
 * Specific adapted {@link ExamFeaturesFile} for Exam 2.4.x
 */
public class ExamFeaturesFile24 extends AbstractExamFeaturesFile {

    public ExamFeaturesFile24(String extension, int startLevel) {
        featuresXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<features name=\"pax-exam-features-"
                    + Info.getPaxExamVersion()
                    + "\">\n"
                    + "<feature name=\"exam\" version=\""
                    + Info.getPaxExamVersion()
                    + "\">\n"
                    + extension + "\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.base/ops4j-base-lang/"
                    + Info.getOps4jBaseVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.base/ops4j-base-monitors/"
                    + Info.getOps4jBaseVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.base/ops4j-base-net/"
                    + Info.getOps4jBaseVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.base/ops4j-base-store/"
                    + Info.getOps4jBaseVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.base/ops4j-base-io/"
                    + Info.getOps4jBaseVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.base/ops4j-base-spi/"
                    + Info.getOps4jBaseVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.base/ops4j-base-util-property/"
                    + Info.getOps4jBaseVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.pax.swissbox/pax-swissbox-core/"
                    + Info.getPaxSwissboxVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.pax.swissbox/pax-swissbox-extender/"
                    + Info.getPaxSwissboxVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.pax.swissbox/pax-swissbox-lifecycle/"
                    + Info.getPaxSwissboxVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.pax.swissbox/pax-swissbox-framework/"
                    + Info.getPaxSwissboxVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.pax.exam/pax-exam/"
                    + Info.getPaxExamVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.pax.exam/pax-exam-extender-service/"
                    + Info.getPaxExamVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.pax.exam/pax-exam-container-rbc/"
                    + Info.getPaxExamVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>wrap:mvn:junit/junit/" + getJunitVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.pax.exam/pax-exam-invoker-junit/"
                    + Info.getPaxExamVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel
                    + "'>mvn:org.openengsb.labs.paxexam.karaf/paxexam-karaf-options/"
                    + getOptionsVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel
                    + "'>mvn:org.apache.geronimo.specs/geronimo-atinject_1.0_spec/" + getInjectionVersion()
                    + "</bundle>\n"
                    + "<bundle start-level='" + startLevel + "'>mvn:org.ops4j.pax.exam/pax-exam-inject/"
                    + Info.getPaxExamVersion() + "</bundle>\n"
                    + "</feature>\n"
                    + "</features>";
    }
}
