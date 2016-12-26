package util;

import dto.LibWebInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibWebConnect {
    // 网址
    public static String libUrl = PropertyUtil.getProperty("LIB_URL");
    // 根据评分排名URL
    public static String bestRated = libUrl + PropertyUtil.getProperty("BEST_RATED");
    // 网站英文名称
    public static String libName = PropertyUtil.getProperty("LIB_NAME");
    // 本地图片root目录
    public static String image_root_path = PropertyUtil.getProperty("IMAGE_ROOT_PATH");
    // 收集超时链接
    public static Set<String> failLibSet = Collections.synchronizedSet(new HashSet<>());

    /**
     * 获取每个网页链接
     *
     * @return webUrlSet
     * @throws IOException
     */
    public static Set<String> getLibUrlSet() {
        Set<String> webUrlSet = new HashSet<>();
        String realUrl = null;
        try {
            for (int i = 1; i <= 25; i++) {
                realUrl = bestRated + i;
                Document doc = Jsoup.connect(realUrl).userAgent("Mozilla").timeout(5 * 1000).get();
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
            failLibSet.add(realUrl);
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
        LibWebInfo libWebInfo = new LibWebInfo();
        try {
            Document doc = Jsoup.connect(libUrl).userAgent("Mozilla").timeout(5 * 1000).get();
            libWebInfo.setUrl(libUrl);
            libWebInfo.setNumber(doc.title().trim().split(" ")[0]);
            // 获取图片链接地址
            Elements imageUrls = doc.select("img[id]");
            for (Element imageUrl : imageUrls) {
                if ("video_jacket_img".equals(imageUrl.attr("id"))) {
                    libWebInfo.setImageUrl(imageUrl.attr("src"));
                }
            }
            // 图片文件
            File imageFile = new File(image_root_path + libWebInfo.getNumber() + ".jpg");
            if (!imageFile.exists()) {
                CommonUtil.downloadImage(libWebInfo.getImageUrl(), imageFile.toString());
            }

            libWebInfo.setTile((doc.title().replace(libName, "").trim()));
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

        } catch (IOException e) {
            System.out.println(libUrl + " Time Out");
            failLibSet.add(libUrl);
        }
        return libWebInfo;
    }

    /**
     * 启用多线程爬取网页信息
     *
     * @param libUrlSet
     * @return
     */
    public static Set<LibWebInfo> getLibWebInfoSet(Set<String> libUrlSet) {
        Set<LibWebInfo> libWebInfoSet = Collections.synchronizedSet(new HashSet<>());
        // 线程上限10
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        // 遍历链接抓取信息
        for (String libUrlStr : libUrlSet) {
            fixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    LibWebInfo libWebInfo = analysis(libUrlStr);
                    if (libWebInfo.getTile() != null) {
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
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return libWebInfoSet;
    }

    /**
     * 单线程爬取网页信息
     *
     * @param libUrlSet
     * @return
     */
    public static Set<LibWebInfo> getLibWebInfoSetSingle(Set<String> libUrlSet) {
        Set<LibWebInfo> libWebInfoSet = new HashSet<>();
        for (String libUrlStr : libUrlSet) {
            System.out.println(libUrlStr);
            LibWebInfo libWebInfo = analysis(libUrlStr);
            if (libWebInfo.getTile() != null) {
                libWebInfoSet.add(libWebInfo);
            }
        }
        return libWebInfoSet;
    }
}
