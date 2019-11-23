package processor;

import dto.LibWebInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import processor.base.PageProcessor;
import util.Const;
import util.JsoupUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static util.Const.LIB_NAME;

/**
 * @Description:
 * @Author Youjun
 * @Create 2019/8/13
 */
@Slf4j
public class LibWebInfoProcessor implements PageProcessor<LibWebInfo> {

    private static final int  POOL_SIZE = 32;

    private BlockingQueue<String> urlQueue;

    private List<LibWebInfo> libWebInfos;

    private ExecutorService fixedThreadPool = new ThreadPoolExecutor(POOL_SIZE,Integer.MAX_VALUE,5, TimeUnit.SECONDS,new LinkedBlockingQueue<>());

    private transient boolean isStart;


    public LibWebInfoProcessor(BlockingQueue<String> urlQueue, List<LibWebInfo> libWebInfos) {
        this.urlQueue = urlQueue;
        this.libWebInfos = libWebInfos;
    }

    @Override
    public LibWebInfo process(String url) throws IOException {
        isStart = true;

//        log.info("get-url:" + Const.LIB_URL + url);

        LibWebInfo libWebInfo = new LibWebInfo();
        Document doc = JsoupUtils.doGet(Const.LIB_URL + url);
        log.info(doc.title());
//            libWebInfo.setUrl(libUrl.substring(libUrl.lastIndexOf("/") + 1));
        libWebInfo.setNo(doc.title().trim().split(" ")[0]);
        libWebInfo.setUrl(Const.LIB_URL + url);
        // 获取图片链接地址
        Elements imageUrls = doc.select("img[id]");
        imageUrls.stream().filter(imageUrl -> "video_jacket_img".equals(imageUrl.attr("id")))
                .forEach(imageUrl -> libWebInfo.setImageUrl("http://" + imageUrl.attr("src").substring(2)));

        libWebInfo.setTitle((doc.title().replace(LIB_NAME, "").trim()));
        // 评分
        if (!doc.select("span.score").isEmpty()) {
            String rated = doc.select("span.score").get(0).text();
            Pattern pattern = Pattern.compile("\\d*\\.\\d*");
            Matcher matcher = pattern.matcher(rated);
            if (matcher.find()) {
                libWebInfo.setRated(matcher.group());
            }
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

        return libWebInfo;
    }

    /**
     * 队列有值时调用，空队列阻塞
     */
    public void onTask() {
        new Thread(() -> {
            while (true) {
                String take;
                try {
                    take = urlQueue.take();
                } catch (InterruptedException e) {
                    log.error(e.toString(), e);
                    continue;
                }
                fixedThreadPool.submit(() -> {
                    try {
                        try {
                            LibWebInfo libWebInfo = this.process(take);
                            libWebInfos.add(libWebInfo);
                        } catch (IOException e) {
                            log.error(Const.LIB_URL+take);
                            urlQueue.put(take);
                        }
                    } catch (InterruptedException e) {
                        log.error(e.toString());
                    }
                });
            }
        }).start();
    }

    public boolean isFinished() {

        ThreadPoolExecutor pool = (ThreadPoolExecutor) this.fixedThreadPool;
        System.out.println(pool.getPoolSize());
        System.out.println(pool.getActiveCount());
        System.out.println(pool.getQueue().size());
        return pool.getQueue().size() == 0 && pool.getActiveCount() == 0 && isStart;
    }
}
