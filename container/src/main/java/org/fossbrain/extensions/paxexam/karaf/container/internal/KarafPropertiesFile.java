package org.fossbrain.extensions.paxexam.karaf.container.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class KarafPropertiesFile {

    private final Properties properties;
    private final File propertyFile;

    public KarafPropertiesFile(File karafHome, String location) {
        propertyFile = new File(karafHome + location);
        properties = new Properties();
    }

    public void load() throws IOException {
        properties.load(new FileInputStream(propertyFile));
    }

    public void put(String key, String value) {
        properties.put(key, value);
    }

    public void extend(String key, String value) {
        properties.put(key, properties.get(key) + value);
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public void store() throws IOException {
        properties.store(new FileOutputStream(propertyFile), "Modified by paxexam");
    }

}
