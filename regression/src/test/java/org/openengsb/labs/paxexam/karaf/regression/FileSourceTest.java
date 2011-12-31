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

package org.openengsb.labs.paxexam.karaf.regression;

import static junit.framework.Assert.assertTrue;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class FileSourceTest {

    @Configuration
    public Option[] config() {
        File createTempFile;
        try {
            createTempFile = File.createTempFile("karaf", ".zip");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        createTempFile.delete();
        try {
            System.setProperty("java.protocol.handler.pkgs", "org.ops4j.pax.url");
            copyInputStreamToFile(new URL("mvn:org.apache.karaf/apache-karaf/2.2.4/zip").openStream(),
                createTempFile);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Option[]{ karafDistributionConfiguration("file://" + createTempFile.toString(), "karaf",
            "2.2.5") };
    }

    // following method copied quick and dirty from commons utils to fix a maven problem
    public static void copyInputStreamToFile(InputStream source, File destination) throws IOException {
        try {
            FileOutputStream output = openOutputStream(destination);
            try {
                copy(source, output);
            } finally {
                closeQuietly(output);
            }
        } finally {
            closeQuietly(source);
        }
    }

    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static FileOutputStream openOutputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && parent.exists() == false) {
                if (parent.mkdirs() == false) {
                    throw new IOException("File '" + file + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file);
    }

    @Test
    public void test() throws Exception {
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("===========================================");
        assertTrue(true);
    }

    @Test
    public void test2() throws Exception {
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXxx");
        assertTrue(true);
    }
}
