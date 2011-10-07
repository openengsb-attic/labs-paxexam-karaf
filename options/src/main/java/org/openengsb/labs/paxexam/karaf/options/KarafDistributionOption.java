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

import static java.lang.String.format;

import org.openengsb.labs.paxexam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.extra.VMOption;

/**
 * Final class to provide an easy and intuitive way to configure the specific karaf distribution options.
 */
public final class KarafDistributionOption {

    /**
     * Configures which distribution options to use. Relevant are the frameworkURL, the frameworkName and the Karaf
     * version since all of those params are relevant to decide which internal configurations to use.
     */
    public static KarafDistributionConfigurationOption karafDistributionConfiguration(String frameworkURL, String name, String karafVersion) {
        return new KarafDistributionConfigurationOption(frameworkURL, name, karafVersion);
    }

    /**
     * Configures which distribution options to use. Relevant are the frameworkURL, the frameworkName and the Karaf
     * version since all of those params are relevant to decide which internal configurations to use.
     */
    public static KarafDistributionConfigurationOption karafDistributionConfiguration() {
        return new KarafDistributionConfigurationOption();
    }

    /**
     * This option allows to configure each configuration fille based on the karaf.home location. The value is "put".
     * Which means it is either replaced or added.
     *
     * If you like to extend an option (e.g. make a=b to a=b,c) please make use of the
     * {@link KarafDistributionConfigurationFileExtendOption}.
     */
    public static Option editConfigurationFilePut(String configurationFilePath, String key, String value) {
        return new KarafDistributionConfigurationFilePutOption(configurationFilePath, key, value);
    }

    /**
     * This option allows to configure each configuration fille based on the karaf.home location. The value is "put".
     * Which means it is either replaced or added.
     *
     * If you like to extend an option (e.g. make a=b to a=b,c) please make use of the
     * {@link KarafDistributionConfigurationFileExtendOption}.
     */
    public static Option editConfigurationFilePut(ConfigurationPointer configurationPointer, String value) {
        return new KarafDistributionConfigurationFilePutOption(configurationPointer, value);
    }

    /**
     * This option allows to extend configurations in each configuration file based on the karaf.home location. The
     * value extends the current value (e.g. a=b to a=a,b) instead of replacing it. If there is no current value it is
     * added.
     *
     * If you would like to have add or replace functionality please use the
     * {@link KarafDistributionConfigurationFilePutOption} instead.
     */
    public static Option editConfigurationFileExtend(String configurationFilePath, String key, String value) {
        return new KarafDistributionConfigurationFileExtendOption(configurationFilePath, key, value);
    }

    /**
     * This option allows to extend configurations in each configuration file based on the karaf.home location. The
     * value extends the current value (e.g. a=b to a=a,b) instead of replacing it. If there is no current value it is
     * added.
     *
     * If you would like to have add or replace functionality please use the
     * {@link KarafDistributionConfigurationFilePutOption} instead.
     */
    public static Option editConfigurationFileExtend(ConfigurationPointer configurationPointer, String value) {
        return new KarafDistributionConfigurationFileExtendOption(configurationPointer, value);
    }

    /**
     * Activates debugging on the embedded Karaf container using the standard 5005 port and holds the vm till you've
     * attached the debugger.
     */
    public static Option debugConfiguration() {
        return debugConfiguration("5005", true);
    }

    /**
     * A very simple and convinient method to set a specific log level without the need of configure the specific option
     * itself.
     */
    public static Option logLevel(LogLevel logLevel) {
        return new LogLevelOption(logLevel);
    }

    /**
     * A very simple and convinient method to set a specific log level without the need of configure the specific option
     * itself.
     */
    public static LogLevelOption logLevel() {
        return new LogLevelOption();
    }

    /**
     * Returns an easy option to activate and configure remote debugging for the Karaf container.
     */
    public static Option debugConfiguration(String port, boolean hold) {
        return new VMOption(format("-Xrunjdwp:transport=dt_socket,server=y,suspend=%s,address=%s", hold ? "y" : "n",
            port));
    }

}
