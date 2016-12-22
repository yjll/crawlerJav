package util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class CommonUtil {
    /**
     * 通过指定URL下载图片
     *
     * @param urlStr
     */
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

    /**
     * 将对象保存到本地
     *
     * @param obj
     * @param path
     */
    public static void saveObject(Object obj, String path) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(PropertyUtil.getProperty(path))));
            out.writeObject(obj);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反序列化
     *
     * @param path
     * @return
     */
    public static Object getObject(String path) {
        Object obj = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(PropertyUtil.getProperty(path))));
            obj = in.readObject();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
