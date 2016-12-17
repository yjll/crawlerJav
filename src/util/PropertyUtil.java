package util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by PC on 12/17/2016.
 */
public class PropertyUtil {
    public static String getProperty(String key) {
        Properties properties = new Properties();
        String value = "";
        try {
            properties.load(PropertyUtil.class.getResourceAsStream("/crawlerJav.ini"));
            value = properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
