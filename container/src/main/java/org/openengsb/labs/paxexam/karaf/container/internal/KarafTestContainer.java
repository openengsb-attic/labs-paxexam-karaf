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

package org.openengsb.labs.paxexam.karaf.container.internal;

import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.rbc.Constants.RMI_HOST_PROPERTY;
import static org.ops4j.pax.exam.rbc.Constants.RMI_NAME_PROPERTY;
import static org.ops4j.pax.exam.rbc.Constants.RMI_PORT_PROPERTY;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.UUID;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.openengsb.labs.paxexam.karaf.options.DoNotModifyLogOption;
import org.openengsb.labs.paxexam.karaf.options.KarafDistributionConfigurationConsoleOption;
import org.openengsb.labs.paxexam.karaf.options.KarafDistributionConfigurationFileOption;
import org.openengsb.labs.paxexam.karaf.options.KarafDistributionConfigurationFilePutOption;
import org.openengsb.labs.paxexam.karaf.options.KarafDistributionConfigurationOption;
import org.openengsb.labs.paxexam.karaf.options.KeepRuntimeFolderOption;
import org.openengsb.labs.paxexam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.RelativeTimeout;
import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TimeoutException;
import org.ops4j.pax.exam.container.remote.RBCRemoteTarget;
import org.ops4j.pax.exam.options.ProvisionOption;
import org.ops4j.pax.exam.options.ServerModeOption;
import org.ops4j.pax.exam.options.SystemPropertyOption;
import org.ops4j.pax.exam.options.extra.VMOption;
import org.ops4j.pax.exam.rbc.client.RemoteBundleContextClient;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

public class KarafTestContainer implements TestContainer {
    private static final String KARAF_TEST_CONTAINER = "KarafTestContainer.start";

    private static final Logger LOGGER = LoggerFactory.getLogger(KarafTestContainer.class);

    private final KarafJavaRunner javaRunner;
    private final RMIRegistry registry;
    private final ExamSystem system;
    private final KarafDistributionConfigurationOption framework;

    private boolean deleteRuntime = true;
    private boolean started = false;
    private RBCRemoteTarget target;

    private File targetFolder;

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
                    systemProperty(RMI_NAME_PROPERTY).value(name),
                    systemProperty("pax.exam.invoker").value("junit"),
                    systemProperty("pax.exam.inject").value("true")
                ));
            target = new RBCRemoteTarget(name, registry.getPort(), subsystem.getTimeout());

            System.setProperty("java.protocol.handler.pkgs", "org.ops4j.pax.url");

            URL sourceDistribution = new URL(framework.getFrameworkURL());

            KeepRuntimeFolderOption[] keepRuntimeFolder = subsystem.getOptions(KeepRuntimeFolderOption.class);
            if (keepRuntimeFolder != null && keepRuntimeFolder.length != 0) {
                deleteRuntime = false;
            }

            targetFolder = retrieveFinalTargetFolder(subsystem);
            extractKarafDistribution(sourceDistribution, targetFolder);

            File javaHome = new File(System.getProperty("java.home"));
            File karafBase = searchKarafBase(targetFolder);
            File karafBin = new File(karafBase + "/bin");
            File featuresXmlFile = new File(targetFolder + "/examfeatures.xml");
            File karafHome = karafBase;
            File deploy = new File(karafBase + "/deploy");
            String karafData = karafHome + "/data";
            // if "%JAVA_MIN_MEM%" == "" (
            // set JAVA_MIN_MEM=128M
            // )
            //
            // if "%JAVA_MAX_MEM%" == "" (
            // set JAVA_MAX_MEM=512M
            // )
            //
            // if "%JAVA_PERM_MEM%" == "" (
            // set JAVA_PERM_MEM=16M
            // )
            //
            // if "%JAVA_MAX_PERM_MEM%" == "" (
            // set JAVA_MAX_PERM_MEM=64M
            // )
            // set DEFAULT_JAVA_OPTS=-server -Xms%JAVA_MIN_MEM% -Xmx%JAVA_MAX_MEM% -XX:PermSize=%JAVA_PERM_MEM%
            // -XX:MaxPermSize=%JAVA_MAX_PERM_MEM% -Dderby.system.home="%KARAF_DATA%\derby"
            // -Dderby.storage.fileSyncTransactionLog=true -Dcom.sun.management.jmxremote
            //
            // set CLASSPATH=%LOCAL_CLASSPATH%;%KARAF_BASE%\conf
            //
            // set DEFAULT_JAVA_DEBUG_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE
            // -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
            String javaOpts = "";
            String[] javaEndorsedDirs =
                new String[]{ javaHome + "/jre/lib/endorsed", javaHome + "/lib/endorsed", karafHome + "/lib/endorsed" };
            String[] javaExtDirs =
                new String[]{ javaHome + "/jre/lib/ext", javaHome + "/lib/ext", javaHome + "/lib/ext" };
            String[] karafOpts = new String[]{};
            ArrayList<String> opts =
                Lists.newArrayList("-Dkaraf.startLocalConsole=" + shouldLocalConsoleBeStarted(subsystem),
                    "-Dkaraf.startRemoteShell=" + shouldRemoteShellBeStarted(subsystem));
            appendVmSettingsFromSystem(opts, subsystem);
            String[] classPath = buildKarafClasspath(karafHome);
            String main = "org.apache.karaf.main.Main";
            String options = "";
            String[] environment = new String[]{};
            String[] fileEndings = new String[]{ "jar", "war", "zip", "kar", "xml" };

            updateLogProperties(karafHome, subsystem);
            updateUserSetProperties(karafHome, subsystem);
            setupExamProperties(karafHome, subsystem);
            makeScriptsInBinExec(karafBin);
            copyReferencedArtifactsToDeployFolder(deploy, subsystem, fileEndings);

            ExamFeaturesFile examFeaturesFile = new ExamFeaturesFile();
            examFeaturesFile.writeToFile(featuresXmlFile);

            examFeaturesFile.adaptDistributionToStartExam(karafHome, featuresXmlFile);

            long startedAt = System.currentTimeMillis();

            javaRunner.exec(environment, karafBase, javaHome.toString(), javaOpts, javaEndorsedDirs, javaExtDirs,
                karafHome.toString(), karafData, karafOpts, opts.toArray(new String[]{}), classPath, main, options);

            LOGGER.debug("Test Container started in " + (System.currentTimeMillis() - startedAt) + " millis");
            LOGGER.info("Wait for test container to finish its initialization " + subsystem.getTimeout());

            if (subsystem.getOptions(ServerModeOption.class).length == 0) {
                waitForState(org.openengsb.labs.paxexam.karaf.container.internal.Constants.SYSTEM_BUNDLE,
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

    private String shouldRemoteShellBeStarted(ExamSystem subsystem) {
        KarafDistributionConfigurationConsoleOption[] consoleOptions =
            subsystem.getOptions(KarafDistributionConfigurationConsoleOption.class);
        if (consoleOptions == null) {
            return "true";
        }
        for (KarafDistributionConfigurationConsoleOption consoleOption : consoleOptions) {
            if (consoleOption.getStartRemoteShell() != null) {
                return consoleOption.getStartRemoteShell() ? "true" : "false";
            }
        }
        return "true";
    }

    private String shouldLocalConsoleBeStarted(ExamSystem subsystem) {
        KarafDistributionConfigurationConsoleOption[] consoleOptions =
            subsystem.getOptions(KarafDistributionConfigurationConsoleOption.class);
        if (consoleOptions == null) {
            return "true";
        }
        for (KarafDistributionConfigurationConsoleOption consoleOption : consoleOptions) {
            if (consoleOption.getStartLocalConsole() != null) {
                return consoleOption.getStartLocalConsole() ? "true" : "false";
            }
        }
        return "true";
    }

    private void makeScriptsInBinExec(File karafBin) {
        if (!karafBin.exists()) {
            return;
        }
        File[] files = karafBin.listFiles();
        for (File file : files) {
            file.setExecutable(true);
        }
    }

    private File retrieveFinalTargetFolder(ExamSystem subsystem) {
        if (framework.getUnpackDirectory() == null) {
            return subsystem.getConfigFolder();
        } else {
            File target = new File(framework.getUnpackDirectory() + "/" + UUID.randomUUID().toString());
            target = transformToAbsolutePath(target);
            target.mkdirs();
            return target;
        }
    }

    private File transformToAbsolutePath(File file) {
        return new File(file.getAbsolutePath());
    }

    private void appendVmSettingsFromSystem(ArrayList<String> opts, ExamSystem subsystem) {
        VMOption[] options = subsystem.getOptions(VMOption.class);
        for (VMOption option : options) {
            opts.add(option.getOption());
        }
    }

    @SuppressWarnings("rawtypes")
    private void copyReferencedArtifactsToDeployFolder(File deploy, ExamSystem subsystem, String[] fileEndings) {
        ProvisionOption[] options = subsystem.getOptions(ProvisionOption.class);
        for (ProvisionOption option : options) {
            try {
                FileUtils.copyURLToFile(new URL(option.getURL()),
                    createFileNameWithRandomPrefixFromUrlAtTarget(option.getURL(), deploy, fileEndings));
            } catch (Exception e) {
                // well, this can happen...
            }
        }
    }

    private File createFileNameWithRandomPrefixFromUrlAtTarget(String url, File deploy, String[] fileEndings) {
        String prefix = UUID.randomUUID().toString();
        String realEnding = extractPossibleFileEndingIfMavenArtifact(url, fileEndings);
        String fileName = new File(url).getName();
        return new File(deploy + "/" + prefix + "_" + fileName + "." + realEnding);
    }

    private String extractPossibleFileEndingIfMavenArtifact(String url, String[] fileEndings) {
        String realEnding = "jar";
        for (String ending : fileEndings) {
            if (url.indexOf("/" + ending + "/") > 0) {
                realEnding = ending;
                break;
            }
        }
        return realEnding;
    }

    private void updateUserSetProperties(File karafHome, ExamSystem subsystem) throws IOException {
        KarafDistributionConfigurationFileOption[] options =
            subsystem.getOptions(KarafDistributionConfigurationFileOption.class);
        ArrayListMultimap<String, KarafDistributionConfigurationFileOption> sortedOptions = ArrayListMultimap.create();
        for (KarafDistributionConfigurationFileOption option : options) {
            sortedOptions.put(option.getConfigurationFilePath(), option);
        }
        Multiset<String> optionSet = sortedOptions.keys();
        for (String optionGroup : optionSet) {
            List<KarafDistributionConfigurationFileOption> singeConfigFileOptions = sortedOptions.get(optionGroup);
            KarafPropertiesFile karafPropertiesFile = new KarafPropertiesFile(karafHome, optionGroup);
            karafPropertiesFile.load();
            for (KarafDistributionConfigurationFileOption optionToApply : singeConfigFileOptions) {
                if (optionToApply instanceof KarafDistributionConfigurationFilePutOption) {
                    karafPropertiesFile.put(optionToApply.getKey(), optionToApply.getValue());
                } else {
                    karafPropertiesFile.extend(optionToApply.getKey(), optionToApply.getValue());
                }
            }
            karafPropertiesFile.store();
        }
    }

    private void setupExamProperties(File karafHome, ExamSystem system) throws IOException {
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
        DoNotModifyLogOption[] modifyLog = system.getOptions(DoNotModifyLogOption.class);
        if (modifyLog != null && modifyLog.length != 0) {
            LOGGER.info("Log file should not be modified by the test framework");
            return;
        }
        String realLogLevel = retrieveRealLogLevel(system);
        File customPropertiesFile = new File(karafHome + "/etc/org.ops4j.pax.logging.cfg");
        Properties karafPropertyFile = new Properties();
        karafPropertyFile.load(new FileInputStream(customPropertiesFile));
        karafPropertyFile.put("log4j.rootLogger", realLogLevel + ", out, stdout, osgi:*");
        karafPropertyFile.store(new FileOutputStream(customPropertiesFile), "updated by pax-exam");
    }

    private String retrieveRealLogLevel(ExamSystem system) {
        LogLevelOption[] logLevelOptions = system.getOptions(LogLevelOption.class);
        return logLevelOptions != null && logLevelOptions.length != 0 ? logLevelOptions[0].getLogLevel().toString()
                : "WARN";
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
        if (sourceDistribution.toExternalForm().indexOf("/zip") > 0) {
            extractZipDistribution(sourceDistribution, targetFolder);
        } else if (sourceDistribution.toExternalForm().indexOf("/tar.gz") > 0) {
            extractTarGzDistribution(sourceDistribution, targetFolder);
        } else {
            throw new IllegalStateException(
                "Unknow packaging of distribution; only zip or tar.gz could be handled.");
        }
    }

    private void extractTarGzDistribution(URL sourceDistribution, File targetFolder) throws IOException,
        FileNotFoundException {
        File uncompressedFile = File.createTempFile("uncompressedTarGz-", ".tar");
        extractGzArchive(sourceDistribution.openStream(), uncompressedFile);
        extract(new TarArchiveInputStream(new FileInputStream(uncompressedFile)), targetFolder);
        FileUtils.forceDelete(uncompressedFile);
    }

    private void extractZipDistribution(URL sourceDistribution, File targetFolder) throws IOException {
        extract(new ZipArchiveInputStream(sourceDistribution.openStream()), targetFolder);
    }

    private void extractGzArchive(InputStream tarGz, File tar) throws IOException {
        BufferedInputStream in = new BufferedInputStream(tarGz);
        FileOutputStream out = new FileOutputStream(tar);
        GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
        final byte[] buffer = new byte[1000];
        int n = 0;
        while (-1 != (n = gzIn.read(buffer))) {
            out.write(buffer, 0, n);
        }
        out.close();
        gzIn.close();
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
            if (deleteRuntime) {
                system.clear();
                try {
                    FileUtils.forceDelete(targetFolder);
                } catch (IOException e) {
                    LOGGER.info("Can't remove runtime system; shedule it for exit of the jvm.");
                    try {
                        FileUtils.forceDeleteOnExit(targetFolder);
                    } catch (IOException e1) {
                        LOGGER.error("Well, this should simply not happen...");
                    }
                }
            }
        }
        return this;
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
