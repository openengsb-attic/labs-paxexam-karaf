/*
 * Copyright 2008 Alin Dreghiciu.
 * Copyright 2009 Toni Menzel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.exam.container.def.internal;

import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

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
import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.Info;
import org.ops4j.pax.exam.RelativeTimeout;
import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TimeoutException;
import org.ops4j.pax.exam.container.remote.RBCRemoteTarget;
import org.ops4j.pax.exam.options.CustomFrameworkOption;
import org.ops4j.pax.exam.options.ServerModeOption;
import org.ops4j.pax.exam.options.SystemPropertyOption;
import org.ops4j.pax.exam.rbc.Constants;
import org.ops4j.pax.exam.rbc.client.RemoteBundleContextClient;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link TestContainer} implementation using Pax Runner.
 * 
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 * @author Toni Menzel (toni@okidokiteam.com)
 * @since 0.3.0, December 09, 2008
 */
public class KarafTestContainer implements TestContainer {

    private static final String RUNNER_TEST_CONTAINER = "PaxRunnerTestContainer.start";
    private static final Logger LOG = LoggerFactory.getLogger(KarafTestContainer.class);
    private static final boolean BLOCKING_RUNNER_INTERNALLY = true;
    public static final int SYSTEM_BUNDLE = 0;

    final private KarafJavaRunner m_javaRunner;
    final private RMIRegistry m_reg;

    private boolean m_started = false;

    /**
     * Underlying Test Target
     */
    private RBCRemoteTarget m_target;

    final private ExamSystem m_system;

    private final CustomFrameworkOption framework;

    /**
     * Constructor.
     * 
     * @param system the PaxExam System to be used
     * @param registry rmiRegistry information to be used in this container (which uses remoting).
     * @param selectedFramework framework to be started in this container.
     */
    public KarafTestContainer(
            final ExamSystem system,
            final RMIRegistry registry,
            final CustomFrameworkOption framework)
    {
        this.framework = framework;
        m_javaRunner = new KarafJavaRunner();
        m_reg = registry;
        m_system = system;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized TestContainer start()
    {
        try {
            String name = m_system.createID(RUNNER_TEST_CONTAINER);

            ExamSystem m_subsystem = m_system.fork(
                options(
                    systemProperty(Constants.RMI_HOST_PROPERTY).value(m_reg.getHost()),
                    systemProperty(Constants.RMI_PORT_PROPERTY).value("" + m_reg.getPort()),
                    systemProperty(Constants.RMI_NAME_PROPERTY).value(name)
                // systemPackage( "org.ops4j.pax.exam;version=" + skipSnapshotFlag( Info.getPaxExamVersion() ) ),
                // systemProperty("java.protocol.handler.pkgs").value("org.ops4j.pax.url")
                ));
            m_target = new RBCRemoteTarget(name, m_reg.getPort(), m_subsystem.getTimeout());

            System.setProperty("java.protocol.handler.pkgs", "org.ops4j.pax.url");

            URL sourceDistribution = new URL(framework.getDefinitionURL());
            File targetFolder = m_subsystem.getTempFolder();
            extractKarafDistribution(sourceDistribution, targetFolder);

            // TODO: adapt those values

            File javaHome = new File(System.getProperty("java.home"));
            File karafBase = searchKarafBase(targetFolder);
            File karafHome = karafBase;
            String karafData = karafHome + "/data";
            File deployFolder = new File(karafBase + "/deploy");
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

            // TODO: update karaf options in etc

            updateKarafProperties(karafHome, m_subsystem);
            updateLogProperties(karafBase, m_subsystem);

            FileUtils.copyURLToFile(
                new URL("mvn:org.ops4j.pax.exam/pax-exam-container-rbc/" + Info.getPaxExamVersion()), new File(
                    deployFolder + "/pax-exam-container-rbc.jar"));
            FileUtils.copyURLToFile(
                new URL("mvn:org.ops4j.pax.exam/pax-exam/" + Info.getPaxExamVersion()), new File(
                    deployFolder + "/pax-exam.jar"));
            FileUtils.copyURLToFile(
                new URL("mvn:org.ops4j.pax.exam/pax-exam-invoker-junit/" + Info.getPaxExamVersion()), new File(
                    deployFolder + "/pax-exam-invoker-junit.jar"));
            FileUtils.copyURLToFile(
                new URL("mvn:org.ops4j.pax.exam/pax-exam-inject/" + Info.getPaxExamVersion()), new File(
                    deployFolder + "/pax-exam-inject.jar"));
            FileUtils.copyURLToFile(
                new URL("mvn:org.ops4j.pax.exam/pax-exam-util/" + Info.getPaxExamVersion()), new File(
                    deployFolder + "/pax-exam-util.jar"));
            FileUtils.copyURLToFile(
                new URL("mvn:org.ops4j.pax.exam/pax-exam-extender-service/" + Info.getPaxExamVersion()), new File(
                    deployFolder + "/pax-exam-extender-service.jar"));

            long startedAt = System.currentTimeMillis();

            m_javaRunner.exec(environment, karafBase, javaHome.toString(), javaOpts, javaEndorsedDirs, javaExtDirs,
                karafHome.toString(), karafData, karafOpts, opts, classPath, main, options);

            LOG.debug("Test Container started in " + (System.currentTimeMillis() - startedAt) + " millis");
            LOG.info("Wait for test container to finish its initialization " + m_subsystem.getTimeout());

            if (m_subsystem.getOptions(ServerModeOption.class).length == 0) {
                waitForState(SYSTEM_BUNDLE, Bundle.ACTIVE, m_subsystem.getTimeout());
            }
            else {
                LOG.info("System runs in Server Mode. Which means, no Test facility bundles available on target system.");
            }

            m_started = true;
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
        karafPropertyFile.put("log4j.rootLogger", "DEBUG, out, osgi:*");
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

    private void printExtraBeforeStart(String[] arguments)
    {
        LOG.debug("Starting up the test container (Pax Runner " + Info.getPaxRunnerVersion() + " )");
        LOG.debug("Pax Runner Arguments: ( " + arguments.length + ")");
        for (String s : arguments) {
            LOG.debug("#   " + s);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized TestContainer stop()
    {
        LOG.debug("Shutting down the test container (Pax Runner)");
        try {
            if (m_started) {
                m_target.stop();
                RemoteBundleContextClient remoteBundleContextClient = m_target.getClientRBC();
                if (remoteBundleContextClient != null) {
                    remoteBundleContextClient.stop();

                }
                if (m_javaRunner != null) {
                    m_javaRunner.shutdown();
                }
            }
            else {
                throw new RuntimeException("Container never came up");
            }
        } finally {
            m_started = false;
            m_target = null;
            m_system.clear();
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    private void waitForState(final long bundleId, final int state, final RelativeTimeout timeout)
        throws TimeoutException
    {
        m_target.getClientRBC().waitForState(bundleId, state, timeout);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void call(TestAddress address)
    {
        m_target.call(address);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized long install(InputStream stream)
    {
        return install("local", stream);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized long install(String location, InputStream stream)
    {
        return m_target.install(location, stream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "KarafTestContainer{" + framework.getDefinitionURL() + "}";
    }
}
