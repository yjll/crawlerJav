package Action;

import dto.LibWebInfo;
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
    public List<String> getLibUrlList() throws IOException {
        List<String> webUrlList = new ArrayList<>();
        String libUrl = PropertyUtil.getProperty("LIBURL");
        // 目标地址
        String bestRated = libUrl + PropertyUtil.getProperty("BEST_RATED");
        for (int i = 1; i <= 1; i++) {
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

    /**
     * 解析网页
     * @param libUrl
     * @return
     */
    public LibWebInfo analysis(String libUrl) {
        LibWebInfo libWebInfo = new LibWebInfo();
        try {
            Document doc = Jsoup.connect(libUrl).userAgent("Mozilla").timeout(5 * 1000).get();
            libWebInfo.setNumber(doc.title().trim().split(" ")[0]);
            libWebInfo.setTile(doc.title());
            // 使用者评价
            String rated = doc.select("span.score").get(0).text();
            rated.replace("(", "");
            rated.replace(")", "");
            libWebInfo.setRated(rated);

            libWebInfo.setDuration(doc.select("span.text").text());

            Elements texts = doc.select("td.text");
            for (Element text : texts) {
                if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", text.text())) {
                    libWebInfo.setDate(text.text());
                }
            }
            // 类别
            List<String> categoryList = new ArrayList<>();
            Elements as = doc.select("a[rel]");
            for (int i = 0; i < as.size(); i++) {
                if ("category tag".equals(as.get(i).attr("rel"))) {
                    categoryList.add(as.get(i).text());
                }
                if ("tag".equals(as.get(as.size() - 1).attr("rel"))) {
                    libWebInfo.setActor(as.get(as.size() - 1).text());
                }
            }
            libWebInfo.setCategoryList(categoryList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return libWebInfo;
    }
}
