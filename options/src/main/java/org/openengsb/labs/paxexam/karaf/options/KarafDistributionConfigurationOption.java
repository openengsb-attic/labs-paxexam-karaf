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

package org.openengsb.labs.paxexam.karaf.options;

import java.io.File;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.MavenUrlReference;

/**
 * Option describing the karaf distribution to use. Without this option no karaf based distribution can be used in exam
 * tests.
 */
public class KarafDistributionConfigurationOption implements Option {

    private String frameworkURL;
    private MavenUrlReference frameworkURLReference;
    private String name;
    private String karafVersion;
    private File unpackDirectory;

    public KarafDistributionConfigurationOption() {
        frameworkURL = null;
        frameworkURLReference = null;
        name = null;
        karafVersion = null;
    }

    public KarafDistributionConfigurationOption(String frameworkURL, String name, String karafVersion) {
        this.frameworkURL = frameworkURL;
        frameworkURLReference = null;
        this.name = name;
        this.karafVersion = karafVersion;
    }

    public KarafDistributionConfigurationOption(MavenUrlReference frameworkURLReference, String name,
            String karafVersion) {
        frameworkURL = null;
        this.frameworkURLReference = frameworkURLReference;
        this.name = name;
        this.karafVersion = karafVersion;
    }

    /**
     * Sets the URL of the framework as a String (for example a file).
     */
    public KarafDistributionConfigurationOption frameworkUrl(String frameworkURL) {
        this.frameworkURL = frameworkURL;
        return this;
    }

    /**
     * Sets the URL of the frameworks as a maven reference.
     */
    public KarafDistributionConfigurationOption frameworkUrl(MavenUrlReference frameworkURL) {
        frameworkURLReference = frameworkURL;
        return this;
    }

    /**
     * Set's the name of the framework. This is only used for logging.
     */
    public KarafDistributionConfigurationOption name(String name) {
        this.name = name;
        return this;
    }

    /**
     * The version of karaf used by the framework. That one is required since there is the high possibility that
     * configuration is different between various karaf versions.
     */
    public KarafDistributionConfigurationOption karafVersion(String karafVersion) {
        this.karafVersion = karafVersion;
        return this;
    }

    /**
     * Define the unpack directory for the karaf distribution. In this directory a UUID named directory will be created
     * for each environment.
     */
    public KarafDistributionConfigurationOption unpackDirectory(File unpackDirectory) {
        this.unpackDirectory = unpackDirectory;
        return this;
    }

    public String getFrameworkURL() {
        if (frameworkURL == null && frameworkURLReference == null) {
            throw new IllegalStateException("Either frameworkurl or frameworkUrlReference need to be set.");
        }
        return frameworkURL != null ? frameworkURL : frameworkURLReference.getURL();
    }

    public String getName() {
        return name;
    }

    public String getKarafVersion() {
        return karafVersion;
    }

    public File getUnpackDirectory() {
        return unpackDirectory;
    }

}
