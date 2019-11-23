package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupUtils {

    public static Document doGet(String url) throws IOException {
        return Jsoup
                .connect(url)
                .userAgent("Mozilla")
                .timeout(120 * 1000)
                .proxy("localhost",1081)
                .get();
    }

    private JsoupUtils(){}
}
