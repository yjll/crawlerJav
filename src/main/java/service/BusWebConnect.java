package service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class BusWebConnect {

    /**
     * 获取列表页面的所有链接
     *
     * @param url
     * @return
     */
    public Set<String> getUrlSet(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(5 * 1000).get();
            // 获取所有链接
            Elements links = doc.select("a.movie-box");
            return links.stream()
                    .map(link -> link.attr("href").substring(link.attr("href").lastIndexOf("/") + 1))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            System.out.println("超时正在重试...");
            getUrlSet(url);
        }
        return null;
    }
}
