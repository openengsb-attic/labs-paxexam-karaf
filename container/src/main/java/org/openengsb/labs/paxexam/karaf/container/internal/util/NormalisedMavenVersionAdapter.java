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

package org.openengsb.labs.paxexam.karaf.container.internal.util;

import org.osgi.framework.Version;

/**
 * This Adapter only affects the constructor of the osgi {@link Version} class. It cuts away the -SNAPSHOT part. This
 * class is used in version analyzing throughout the framework.
 */
public class NormalisedMavenVersionAdapter extends Version {

    private static final String VERSION_PATTERN = "[^0-9.]";

    public NormalisedMavenVersionAdapter(int major, int minor, int micro, String qualifier) {
        super(major, minor, micro, qualifier);
    }

    public NormalisedMavenVersionAdapter(int major, int minor, int micro) {
        super(major, minor, micro);
    }

    public NormalisedMavenVersionAdapter(String version) {
        super(version.replace("_", ".").replaceAll(VERSION_PATTERN, ""));
    }
}
