package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertyUtil {

    private static final Properties properties;

    static {
        properties = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("crawlerJav.ini");
             BufferedReader bf = new BufferedReader(new InputStreamReader(in))) {
            properties.load(bf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    private PropertyUtil() {
    }
}
