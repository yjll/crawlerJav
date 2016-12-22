package Action;

import dto.LibWebInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.PropertyUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibWebConnect {
    /**
     * 获取每个网页链接
     *
     * @return webUrlSet
     * @throws IOException
     */
    public static Set<String> getLibUrlSet() {
        Set<String> webUrlSet = new HashSet<>();
        String libUrl = PropertyUtil.getProperty("LIB_URL");
        // 目标地址
        String bestRated = libUrl + PropertyUtil.getProperty("BEST_RATED");
        try {
            for (int i = 1; i <= 25; i++) {
                Document doc = Jsoup.connect(bestRated + i).userAgent("Mozilla").timeout(5 * 1000).get();
                // 获取所有链接
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    if (Pattern.matches(".*v=.*", link.attr("href"))) {
                        // 真实地址
                        webUrlSet.add(libUrl + link.attr("href").substring(2));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return webUrlSet;
    }

    /**
     * 解析网页
     *
     * @param libUrl
     * @return
     */
    public static LibWebInfo analysis(String libUrl) {
        LibWebInfo libWebInfo = null ;
        try {
            Document doc = Jsoup.connect(libUrl).userAgent("Mozilla").timeout(5 * 1000).get();
            libWebInfo = new LibWebInfo();
            libWebInfo.setUrl(libUrl);
            libWebInfo.setNumber(doc.title().trim().split(" ")[0]);
            libWebInfo.setTile((doc.title().replace(PropertyUtil.getProperty("LIB_NAME"), "").trim()));
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
            for (Element a : as) {
                if ("category tag".equals(a.attr("rel"))) {
                    categoryList.add(a.text());
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
            System.out.println(libUrl + " Time Out");
            e.printStackTrace();
        }
        return libWebInfo;
    }

    /**
     * 启用多线程爬取网页信息
     * @param libUrlSet
     * @return
     */
    public static Set<LibWebInfo> getLibWebInfoSet(Set<String> libUrlSet){
        Set<LibWebInfo> libWebInfoSet = Collections.synchronizedSet(new HashSet<>());
        // 线程上限4
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
        // 遍历链接抓取信息
        for (String libUrl : libUrlSet) {
            fixedThreadPool.execute(new Runnable() {
                public void run() {
                    LibWebInfo libWebInfo = analysis(libUrl);
                    if (libWebInfo != null) {
                        libWebInfoSet.add(libWebInfo);
                    }
                }
            });
        }
        // 线程池关闭
        fixedThreadPool.shutdown();
        while (true) {
            // 确认线程结束
            if (fixedThreadPool.isTerminated()) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return libWebInfoSet;
    }

}
