package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private Properties properties;

    public ConfigReader(String environment) {
        properties = new Properties();
        String filePath = "src/test/resources/data/" + environment + ".properties";
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Could not read property file for environment: " + environment, e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
