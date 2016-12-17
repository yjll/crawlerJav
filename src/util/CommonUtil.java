package util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class CommonUtil {
    public static void dowsloadImage(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            InputStream in = conn.getInputStream();
            byte[] b = new byte[1024];
            OutputStream out = new BufferedOutputStream(new FileOutputStream("D:/a.jpg"));
            int len;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            in.close();
            out.close();
            System.out.println("=====");

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

}
