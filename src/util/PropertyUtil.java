package util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
    public static String getProperty(String key) {
        Properties properties = new Properties();
        String value = null;
        try {
            InputStream in = new FileInputStream("/IDEAProject/crawlerJavDate/crawlerJav.ini");
            properties.load(in);
            in.close();
            value = properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
