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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.ops4j.io.Pipe;

/**
 * Very simple asynchronous implementation of Java Runner. Exec is being invoked in a fresh Thread.
 */
public class KarafJavaRunner {

    private InternalRunner runner;

    public KarafJavaRunner() {
        runner = new InternalRunner();
    }

    public synchronized void
        exec(final String[] environment, final File karafBase, final String javaHome, final String javaOpts,
                final String[] javaEndorsedDirs,
                final String[] javaExtDirs, final String karafHome, final String karafData, final String karafOpts,
                final String opts, final String[] classPath, final String main, final String options) {
        new Thread("KarafJavaRunner") {
            @Override
            public void run() {
                String cp = buildCmdSeparatedString(classPath);
                String endDirs = buildCmdSeparatedString(javaEndorsedDirs);
                String extDirs = buildCmdSeparatedString(javaExtDirs);

                // exec "$JAVA" $JAVA_OPTS -Djava.endorsed.dirs="${JAVA_ENDORSED_DIRS}"
                // -Djava.ext.dirs="${JAVA_EXT_DIRS}" -Dkaraf.instances="${KARAF_HOME}/instances"
                // -Dkaraf.home="$KARAF_HOME" -Dkaraf.base="$KARAF_BASE" -Dkaraf.data="$KARAF_DATA"
                // -Djava.util.logging.config.file="$KARAF_BASE/etc/java.util.logging.properties" $KARAF_OPTS $OPTS
                // -classpath "$CLASSPATH" $MAIN "$@"
                final CommandLineBuilder commandLine = new CommandLineBuilder()
                    .append(getJavaExecutable(javaHome))
                    .append(javaOpts)
                    .append("-Djava.endorsed.dirs=" + endDirs)
                    .append("-Djava.ext.dirs=" + extDirs)
                    .append("-Dkaraf.instances=" + karafHome + "/instances")
                    .append("-Dkaraf.home=" + karafHome)
                    .append("-Dkaraf.base=" + karafBase)
                    .append("-Dkaraf.data=" + karafData)
                    .append("-Djava.util.logging.config.file=" + karafBase + "/etc/java.util.logging.properties")
                    .append(karafOpts)
                    .append(opts)
                    .append("-cp")
                    .append(cp)
                    .append(main)
                    .append(options);

                runner.exec(commandLine, karafBase, environment);
            }

            private String buildCmdSeparatedString(final String[] splitted) {
                final StringBuilder together = new StringBuilder();
                for (String path : splitted)
                {
                    if (together.length() != 0)
                    {
                        together.append(File.pathSeparator);
                    }
                    together.append(path);
                }
                return together.toString();
            }

            private String getJavaExecutable(final String javaHome)
            {
                if (javaHome == null)
                {
                    throw new IllegalStateException("JAVA_HOME is not set.");
                }
                return javaHome + "/bin/java";
            }
        }.start();
    }

    public synchronized void shutdown() {
        runner.shutdown();
    }

    private class InternalRunner {
        private Process m_frameworkProcess;
        private Thread m_shutdownHook;

        public synchronized void exec(CommandLineBuilder commandLine, final File workingDirectory,
                final String[] envOptions) {
            if (m_frameworkProcess != null)
            {
                throw new IllegalStateException("Platform already started");
            }

            try
            {
                m_frameworkProcess =
                    Runtime.getRuntime().exec(commandLine.toArray(), createEnvironmentVars(envOptions),
                        workingDirectory);
            } catch (IOException e)
            {
                throw new IllegalStateException("Could not start up the process", e);
            }

            m_shutdownHook = createShutdownHook(m_frameworkProcess);
            Runtime.getRuntime().addShutdownHook(m_shutdownHook);

            waitForExit();
        }

        private String[] createEnvironmentVars(String[] envOptions)
        {
            List<String> env = new ArrayList<String>();
            Map<String, String> getenv = System.getenv();
            for (String key : getenv.keySet()) {
                env.add(key + "=" + getenv.get(key));
            }
            if (envOptions != null) {
                Collections.addAll(env, envOptions);
            }
            return env.toArray(new String[env.size()]);
        }

        /**
         * {@inheritDoc}
         */
        public void shutdown()
        {
            try {
                if (m_shutdownHook != null) {
                    synchronized (m_shutdownHook) {
                        if (m_shutdownHook != null) {
                            Runtime.getRuntime().removeShutdownHook(m_shutdownHook);
                            m_frameworkProcess = null;
                            m_shutdownHook.run();
                            m_shutdownHook = null;
                        }
                    }
                }
            } catch (IllegalStateException ignore)
            {
                // just ignore
            }
        }

        /**
         * Wait till the framework process exits.
         */
        public void waitForExit()
        {
            synchronized (m_frameworkProcess) {
                try
                {
                    m_frameworkProcess.waitFor();
                    shutdown();
                } catch (Throwable e)
                {
                    shutdown();
                }
            }
        }

        /**
         * Create helper thread to safely shutdown the external framework process
         * 
         * @param process framework process
         * 
         * @return stream handler
         */
        private Thread createShutdownHook(final Process process)
        {
            final Pipe errPipe = new Pipe(process.getErrorStream(), System.err).start("Error pipe");
            final Pipe outPipe = new Pipe(process.getInputStream(), System.out).start("Out pipe");
            final Pipe inPipe = new Pipe(process.getOutputStream(), System.in).start("In pipe");

            return new Thread(
                new Runnable()
                {
                    public void run()
                    {
                        inPipe.stop();
                        outPipe.stop();
                        errPipe.stop();

                        try
                        {
                            process.destroy();
                        }
                        catch (Exception e)
                        {
                            // ignore if already shutting down
                        }
                    }
                },
                "Pax-Runner shutdown hook");
        }

    }

    private class CommandLineBuilder
    {

        /**
         * The command line array.
         */
        private String[] m_commandLine;

        /**
         * Creates a new command line builder.
         */
        public CommandLineBuilder()
        {
            m_commandLine = new String[0];
        }

        /**
         * Appends an array of strings to command line.
         * 
         * @param segments array to append
         * 
         * @return CommandLineBuilder for fluent api
         */
        private CommandLineBuilder append(final String[] segments)
        {
            if (segments != null && segments.length > 0)
            {
                final String[] command = new String[m_commandLine.length + segments.length];
                System.arraycopy(m_commandLine, 0, command, 0, m_commandLine.length);
                System.arraycopy(segments, 0, command, m_commandLine.length, segments.length);
                m_commandLine = command;
            }
            return this;
        }

        /**
         * Appends a string to command line.
         * 
         * @param segment string to append
         * 
         * @return CommandLineBuilder for fluent api
         */
        public CommandLineBuilder append(final String segment)
        {
            if (segment != null && !segment.isEmpty())
            {
                return append(new String[]{ segment });
            }
            return this;
        }

        /**
         * Returns the command line.
         * 
         * @return command line
         */
        public String[] toArray()
        {
            return m_commandLine;
        }

    }
}
