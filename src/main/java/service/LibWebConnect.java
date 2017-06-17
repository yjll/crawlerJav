package service;

import dto.LibWebInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.CommonUtil;
import util.PropertyUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static util.Count.*;

public class LibWebConnect {


    // 收集超时链接
    public Set<String> failLibSet = Collections.synchronizedSet(new HashSet<>());

    /**
     * 获取高分链接
     *
     * @return webUrlSet
     * @throws IOException
     */
    public Set<String> getTopLibUrlSet() {
        Set<String> webUrlSet = new HashSet<>();
        for (int i = 1; i <= 25; i++) {
            webUrlSet.addAll(getUrlSet(BEST_RATED + i));
        }
        return webUrlSet;

    }

    /**
     * 根据No查询出真实的链接
     *
     * @param numbers
     * @return
     */
    public Set<String> getLibWebInfoByNo(Set<String> numbers) {
        Set<String> libUrlSet = new HashSet<>();
        failLibSet.clear();
        if (numbers.isEmpty()) {
            return libUrlSet;
        } else {
            try {
                for (String s : numbers) {
                    Document doc = Jsoup.connect(SEARCH_BY_NO + s).userAgent("Mozilla").timeout(5 * 1000).get();
                    doc.select("meta[property]").stream()
                            .filter(element -> "og:url".equals(element.attr("property")))
                            .map(element -> element.attr("content"))
                            .map(element -> element.substring(element.lastIndexOf("/") + 1))
                            .forEach(libUrlSet::add);
                }
            } catch (IOException e) {
                numbers.forEach(failLibSet::add);
                System.out.println("超时正在重试...");
            }
            // 递归根据No取链接，直到failLibSet为空
            libUrlSet.addAll(getLibWebInfoByNo(failLibSet));
            return libUrlSet;
        }

    }


    /**
     * 获取列表页面的所有链接
     *
     * @param url
     * @return
     */
    private Set<String> getUrlSet(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(5 * 1000).get();
            // 获取所有链接
            Elements links = doc.select("a[href]");
            return links.stream()
                    .filter(link -> Pattern.matches(".*v=.*", link.attr("href")))
                    .map(link -> link.attr("href").substring(2))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            System.out.println("超时正在重试...");
            getUrlSet(url);
        }
        return null;
    }


    /**
     * 解析网页
     *
     * @param libUrl
     * @return
     */
    private LibWebInfo analysis(String libUrl) {
        LibWebInfo libWebInfo = new LibWebInfo();
        try {
            Document doc = Jsoup.connect(libUrl).userAgent("Mozilla").timeout(5 * 1000).get();
            libWebInfo.setUrl(libUrl.substring(libUrl.lastIndexOf("/") + 1));
            libWebInfo.setNumber(doc.title().trim().split(" ")[0]);
            // 获取图片链接地址
            Elements imageUrls = doc.select("img[id]");
            imageUrls.stream().filter(imageUrl -> "video_jacket_img".equals(imageUrl.attr("id")))
                    .forEach(imageUrl -> libWebInfo.setImageUrl(imageUrl.attr("src")));
            // 图片文件
            File imageFile = new File(IMAGE_ROOT_PATH + libWebInfo.getNumber() + ".jpg");
            if (!imageFile.exists()) {
                CommonUtil.downloadImage(libWebInfo.getImageUrl(), imageFile.toString());
            }

            libWebInfo.setTile((doc.title().replace(LIB_NAME, "").trim()));
            // 评分
            String rated = doc.select("span.score").get(0).text();
            Pattern pattern = Pattern.compile("\\d*\\.\\d*");
            Matcher matcher = pattern.matcher(rated);
            if (matcher.find()) {
                libWebInfo.setRated(matcher.group());
            }
            // 时长
            libWebInfo.setDuration(doc.select("span.text").text());

            // 日期
            Elements texts = doc.select("td.text");
            texts.stream().filter(text -> Pattern.matches("\\d{4}-\\d{2}-\\d{2}", text.text()))
                    .forEach(text -> libWebInfo.setDate(text.text()));

            // 类别
            Elements as = doc.select("a[rel]");
            List<String> categoryList = as.stream()
                    .filter(a -> "category tag".equals(a.attr("rel")))
                    .map(Element::text)
                    .collect(Collectors.toList());
            libWebInfo.setCategoryList(categoryList);

            // 演员
            Elements actors = doc.select("a[href]");
            List<String> actorList = actors.stream()
                    .filter(actor -> actor.attr("href").startsWith("vl_star"))
                    .map(Element::text)
                    .collect(Collectors.toList());
            libWebInfo.setActorList(actorList);

        } catch (IOException e) {
            System.out.println(libUrl + " Time Out");
            failLibSet.add(libUrl);
        }
        return libWebInfo;
    }

    public Set<LibWebInfo> getDbSet(Set<String> newLibUrlSet) {
        // 页面信息
        Set<LibWebInfo> libWebInfoSet;
        // 插入数据库数据
        Set<LibWebInfo> dbSet = new HashSet<>();
        while (true) {
            System.out.println("The size of newLibUrlSet is " + newLibUrlSet.size());
            failLibSet.clear();
            // 获取页面信息
            libWebInfoSet = getLibWebInfoSet(newLibUrlSet);

            // 已获取到的页面的地址
            dbSet.addAll(libWebInfoSet);

            if (failLibSet.size() == 0) {
                break;
            } else {
                System.out.println("超时URL:");
                failLibSet.forEach(System.out::println);
            }
            // 根据失败的链接重新获取信息
            newLibUrlSet = failLibSet;
        }
        return dbSet;
    }

    /**
     * 启用多线程爬取网页信息
     *
     * @param libUrlSet
     * @return
     */
    private Set<LibWebInfo> getLibWebInfoSet(Set<String> libUrlSet) {
        Set<LibWebInfo> libWebInfoSet = Collections.synchronizedSet(new HashSet<>());
        // 线程上限10
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        List<Future> futureList = new ArrayList<>();
        // 遍历链接抓取信息
        for (String libUrlStr : libUrlSet) {
            Future<String> submit = fixedThreadPool.submit(() -> {
                LibWebInfo libWebInfo = analysis(LIB_URL + libUrlStr);
                if (libWebInfo.getTile() != null) {
                    libWebInfoSet.add(libWebInfo);
                }
                return libWebInfo.getNumber();
            });
            futureList.add(submit);

        }
        // 线程池关闭
        fixedThreadPool.shutdown();
        // 已爬去完毕的影片番号
        List<String> noList = new ArrayList<>();
        for (Future future : futureList) {
            try {
                String no = (String) future.get(5000, TimeUnit.MILLISECONDS);
                System.out.println(no + "下载完成") ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
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
    private Set<LibWebInfo> getLibWebInfoSetSingle(Set<String> libUrlSet) {
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
