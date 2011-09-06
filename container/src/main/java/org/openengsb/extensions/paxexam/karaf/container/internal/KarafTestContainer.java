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

package org.openengsb.extensions.paxexam.karaf.container.internal;

import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.rbc.Constants.RMI_HOST_PROPERTY;
import static org.ops4j.pax.exam.rbc.Constants.RMI_NAME_PROPERTY;
import static org.ops4j.pax.exam.rbc.Constants.RMI_PORT_PROPERTY;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.openengsb.extensions.paxexam.karaf.options.KarafDistributionConfigurationOption;
import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.RelativeTimeout;
import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TimeoutException;
import org.ops4j.pax.exam.container.remote.RBCRemoteTarget;
import org.ops4j.pax.exam.options.ServerModeOption;
import org.ops4j.pax.exam.options.SystemPropertyOption;
import org.ops4j.pax.exam.rbc.client.RemoteBundleContextClient;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KarafTestContainer implements TestContainer {
    private static final String KARAF_TEST_CONTAINER = "KarafTestContainer.start";

    private static final Logger LOGGER = LoggerFactory.getLogger(KarafTestContainer.class);

    private final KarafJavaRunner javaRunner;
    private final RMIRegistry registry;
    private final ExamSystem system;
    private final KarafDistributionConfigurationOption framework;

    private boolean started = false;
    private RBCRemoteTarget target;

    public KarafTestContainer(ExamSystem system, RMIRegistry registry, KarafDistributionConfigurationOption framework) {
        this.framework = framework;
        this.registry = registry;
        this.system = system;

        javaRunner = new KarafJavaRunner();
    }

    @Override
    public synchronized TestContainer start() {
        try {
            String name = system.createID(KARAF_TEST_CONTAINER);

            ExamSystem subsystem = system.fork(
                options(
                    systemProperty(RMI_HOST_PROPERTY).value(registry.getHost()),
                    systemProperty(RMI_PORT_PROPERTY).value("" + registry.getPort()),
                    systemProperty(RMI_NAME_PROPERTY).value(name)
                ));
            target = new RBCRemoteTarget(name, registry.getPort(), subsystem.getTimeout());

            System.setProperty("java.protocol.handler.pkgs", "org.ops4j.pax.url");

            URL sourceDistribution = new URL(framework.getFrameworkURL());
            File targetFolder = subsystem.getTempFolder();
            extractKarafDistribution(sourceDistribution, targetFolder);

            File javaHome = new File(System.getProperty("java.home"));
            File karafBase = searchKarafBase(targetFolder);
            File featuresXmlFile = new File(targetFolder + "/examfeatures.xml");
            File karafHome = karafBase;
            String karafData = karafHome + "/data";
            String javaOpts = "";
            String[] javaEndorsedDirs =
                new String[]{ javaHome + "/jre/lib/endorsed", javaHome + "/lib/endorsed", karafHome + "/lib/endorsed" };
            String[] javaExtDirs =
                new String[]{ javaHome + "/jre/lib/ext", javaHome + "/lib/ext", javaHome + "/lib/ext" };
            String karafOpts = "";
            String opts = "";
            String[] classPath = buildKarafClasspath(karafHome);
            String main = "org.apache.karaf.main.Main";
            String options = "";
            String[] environment = new String[]{};

            updateKarafProperties(karafHome, subsystem);
            updateLogProperties(karafBase, subsystem);

            ExamFeaturesFile examFeaturesFile = new ExamFeaturesFile();
            examFeaturesFile.writeToFile(featuresXmlFile);

            File backupDir = createTempDirectory();
            FileUtils.copyDirectory(targetFolder, backupDir);

            examFeaturesFile.adaptDistributionToStartExam(backupDir, featuresXmlFile);
            examFeaturesFile.adaptDistributionToStartExam(karafHome, featuresXmlFile);

            long startedAt = System.currentTimeMillis();

            javaRunner.exec(environment, karafBase, javaHome.toString(), javaOpts, javaEndorsedDirs, javaExtDirs,
                karafHome.toString(), karafData, karafOpts, opts, classPath, main, options);

            LOGGER.debug("Test Container started in " + (System.currentTimeMillis() - startedAt) + " millis");
            LOGGER.info("Wait for test container to finish its initialization " + subsystem.getTimeout());

            if (subsystem.getOptions(ServerModeOption.class).length == 0) {
                waitForState(org.openengsb.extensions.paxexam.karaf.container.internal.Constants.SYSTEM_BUNDLE,
                    Bundle.ACTIVE, subsystem.getTimeout());
            }
            else {
                LOGGER
                    .info("System runs in Server Mode. Which means, no Test facility bundles available on target system.");
            }

            started = true;
        } catch (IOException e) {
            throw new RuntimeException("Problem starting container", e);
        }
        return this;
    }

    private void updateKarafProperties(File karafHome, ExamSystem system) throws IOException {
        File customPropertiesFile = new File(karafHome + "/etc/system.properties");
        SystemPropertyOption[] customProps = system.getOptions(SystemPropertyOption.class);
        Properties karafPropertyFile = new Properties();
        karafPropertyFile.load(new FileInputStream(customPropertiesFile));
        for (SystemPropertyOption systemPropertyOption : customProps) {
            karafPropertyFile.put(systemPropertyOption.getKey(), systemPropertyOption.getValue());
        }
        karafPropertyFile.store(new FileOutputStream(customPropertiesFile), "updated by pax-exam");
    }

    private void updateLogProperties(File karafHome, ExamSystem system) throws IOException {
        File customPropertiesFile = new File(karafHome + "/etc/org.ops4j.pax.logging.cfg");
        Properties karafPropertyFile = new Properties();
        karafPropertyFile.load(new FileInputStream(customPropertiesFile));
        karafPropertyFile.put("log4j.rootLogger", "WARN, out, stdout, osgi:*");
        karafPropertyFile.store(new FileOutputStream(customPropertiesFile), "updated by pax-exam");
    }

    private String[] buildKarafClasspath(File karafHome) {
        List<String> cp = new ArrayList<String>();
        File[] jars = new File(karafHome + "/lib").listFiles((FileFilter) new WildcardFileFilter("karaf*.jar"));
        for (File jar : jars) {
            cp.add(jar.toString());
        }
        return cp.toArray(new String[]{});
    }

    /**
     * Since we might get quite deep use a simple breath first search algorithm
     */
    private File searchKarafBase(File targetFolder) {
        Queue<File> searchNext = new LinkedList<File>();
        searchNext.add(targetFolder);
        while (!searchNext.isEmpty()) {
            File head = searchNext.poll();
            if (!head.isDirectory()) {
                continue;
            }
            boolean system = false;
            boolean etc = false;
            for (File file : head.listFiles()) {
                if (file.isDirectory() && file.getName().equals("system")) {
                    system = true;
                }
                if (file.isDirectory() && file.getName().equals("etc")) {
                    etc = true;
                }
            }
            if (system && etc) {
                return head;
            }
            searchNext.addAll(Arrays.asList(head.listFiles()));
        }
        throw new IllegalStateException("No karaf base dir found in extracted distribution.");
    }

    private void extractKarafDistribution(URL sourceDistribution, File targetFolder) throws IOException {
        if (sourceDistribution.toExternalForm().endsWith("zip")) {
            extract(new ZipArchiveInputStream(sourceDistribution.openStream()), targetFolder);
        } else if (sourceDistribution.toExternalForm().endsWith("tar.gz")) {
            extract(new TarArchiveInputStream(sourceDistribution.openStream()), targetFolder);
        } else {
            throw new IllegalStateException(
                "Unknow packaging of distribution; only zip or tar.gz could be handled.");
        }
    }

    private void extract(ArchiveInputStream is, File targetDir) throws IOException {
        try {
            if (targetDir.exists()) {
                FileUtils.forceDelete(targetDir);
            }
            targetDir.mkdirs();
            ArchiveEntry entry = is.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                name = name.substring(name.indexOf("/") + 1);
                File file = new File(targetDir, name);
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    OutputStream os = new FileOutputStream(file);
                    try {
                        IOUtils.copy(is, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
                entry = is.getNextEntry();
            }
        } finally {
            is.close();
        }
    }

    @Override
    public synchronized TestContainer stop() {
        LOGGER.debug("Shutting down the test container (Pax Runner)");
        try {
            if (started) {
                target.stop();
                RemoteBundleContextClient remoteBundleContextClient = target.getClientRBC();
                if (remoteBundleContextClient != null) {
                    remoteBundleContextClient.stop();

                }
                if (javaRunner != null) {
                    javaRunner.shutdown();
                }
            }
            else {
                throw new RuntimeException("Container never came up");
            }
        } finally {
            started = false;
            target = null;
            system.clear();
        }
        return this;
    }

    private static File createTempDirectory() throws IOException {
        final File temp;
        temp = File.createTempFile("examkarafbackup", Long.toString(System.nanoTime()));
        if (!temp.delete()) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        if (!temp.mkdir()) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
        return temp;
    }

    private void waitForState(final long bundleId, final int state, final RelativeTimeout timeout)
        throws TimeoutException {
        target.getClientRBC().waitForState(bundleId, state, timeout);
    }

    @Override
    public synchronized void call(TestAddress address) {
        target.call(address);
    }

    @Override
    public synchronized long install(InputStream stream) {
        return install("local", stream);
    }

    @Override
    public synchronized long install(String location, InputStream stream) {
        return target.install(location, stream);
    }

    @Override
    public String toString() {
        return "KarafTestContainer{" + framework.getFrameworkURL() + "}";
    }
}
