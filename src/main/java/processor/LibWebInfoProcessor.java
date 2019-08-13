package processor;

import dto.LibWebInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import processor.base.PageProcessor;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
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

    private BlockingQueue<String> urlQueue;

    private List<LibWebInfo> libWebInfos;

    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

    public LibWebInfoProcessor(BlockingQueue<String> urlQueue, List<LibWebInfo> libWebInfos) {
        this.urlQueue = urlQueue;
        this.libWebInfos = libWebInfos;
    }

    @Override
    public LibWebInfo process(String url) throws IOException {
        LibWebInfo libWebInfo = new LibWebInfo();
        Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(5 * 1000).get();
//            libWebInfo.setUrl(libUrl.substring(libUrl.lastIndexOf("/") + 1));
        libWebInfo.setNo(doc.title().trim().split(" ")[0]);
        // 获取图片链接地址
        Elements imageUrls = doc.select("img[id]");
        imageUrls.stream().filter(imageUrl -> "video_jacket_img".equals(imageUrl.attr("id")))
                .forEach(imageUrl -> libWebInfo.setImageUrl(imageUrl.attr("src")));

        libWebInfo.setTitle((doc.title().replace(LIB_NAME, "").trim()));
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
                    log.error(e.toString(),e);
                    continue;
                }
                String finalTake = take;
                fixedThreadPool.submit(() -> {
                    try {
                        try {
                            LibWebInfo libWebInfo = this.process(finalTake);
                            if (Objects.nonNull(libWebInfo)) {
                                libWebInfos.add(libWebInfo);
                            }
                        } catch (IOException e) {
                            log.error(e.toString(), e);
                            urlQueue.put(finalTake);
                        }
                    } catch (InterruptedException e) {
                        log.error(e.toString(), e);
                    }
                });
            }
        }).start();
    }

    public boolean isFinished(){
        ThreadPoolExecutor pool = (ThreadPoolExecutor) this.fixedThreadPool;
        return pool.getQueue().size() == 0 && pool.getActiveCount() == 0;
    }
}
