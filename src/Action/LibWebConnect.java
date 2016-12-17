package Action;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.PropertyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibWebConnect {
    /**
     * 获取每个网页链接
     *
     * @return urlList
     * @throws IOException
     */
    public List<String> getWebUrlList() throws IOException {
        List<String> webUrlList = new ArrayList<>();
        String libUrl = PropertyUtil.getProperty("LIBURL");
        // 目标地址
        String bestRated = libUrl + PropertyUtil.getProperty("BEST_RATED");
        for (int i = 1; i <= 2; i++) {
            Document doc = Jsoup.connect(bestRated + i).userAgent("Mozilla").timeout(5 * 1000).get();
            // 获取所有链接
            Elements links = doc.select("a[href]");
            Pattern linkPattern = Pattern.compile(".*v=.*");
            Matcher linkMatcher;
            for (Element link : links) {
                linkMatcher = linkPattern.matcher(link.attr("href"));
                if (linkMatcher.matches()) {
                    // 真实地址
                    webUrlList.add(libUrl + link.attr("href").substring(2) + "\n");
                }
            }
        }
        return webUrlList;

    }

}
