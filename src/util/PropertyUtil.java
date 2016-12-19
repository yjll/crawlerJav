package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
    public static String getProperty(String key) {
        Properties properties = new Properties();
        String value = "";
        try {
            InputStream in =PropertyUtil.class.getResourceAsStream("/crawlerJav.ini");
            properties.load(in);
            in.close();
            value = properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
