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
            for (Element link : links) {
                if (Pattern.matches(".*v=.*", link.attr("href"))) {
                    // 真实地址
                    webUrlList.add(libUrl + link.attr("href").substring(2) + "\n");
                }
            }
        }
        return webUrlList;
    }

    /**
     * 解析网页
     *
     * @param libUrl
     * @return
     */
    public LibWebInfo analysis(String libUrl) {
        LibWebInfo libWebInfo = new LibWebInfo();
        try {
            Document doc = Jsoup.connect(libUrl).userAgent("Mozilla").timeout(5 * 1000).get();
            libWebInfo.setNumber(doc.title().trim().split(" ")[0]);
            libWebInfo.setTile((doc.title().replace(PropertyUtil.getProperty("LIBNAME"), "").trim()));
            // 评分
            String rated = doc.select("span.score").get(0).text();
            Pattern pattern = Pattern.compile("\\d*\\.\\d*");
            Matcher matcher = pattern.matcher(rated);
            if (matcher.find()) {
                libWebInfo.setRated(matcher.group());
            }
            // 时长
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
            }
            libWebInfo.setCategoryList(categoryList);

            // 演员
            List<String> actorList = new ArrayList<>();
            Elements actors = doc.select("a[href]");
            for (Element actor : actors) {
                if (actor.attr("href").startsWith("vl_star")) {
                    actorList.add(actor.text());
                }
            }
            libWebInfo.setActorList(actorList);

            // 获取图片链接地址
            Elements imageUrls = doc.select("img[id]");
            for (Element imageUrl : imageUrls) {
                if ("video_jacket_img".equals(imageUrl.attr("id"))) {
                    libWebInfo.setImageUrl(imageUrl.attr("src"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return libWebInfo;
    }
}
