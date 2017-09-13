package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    private static final Properties properties;

    static {
        properties = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/crawlerJav.ini");
        try {
            properties.load(in);
            in.close();
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
