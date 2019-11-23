package processor;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import processor.base.PageProcessor;
import util.Const;
import util.JsoupUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author Youjun
 * @Create 2019/8/13
 */
@Slf4j
public class LibWebUrlProcessor implements PageProcessor<Collection<String>> {

    private BlockingQueue<String> urlListQueue;
    private BlockingQueue<String> urlQueue;


    public LibWebUrlProcessor(BlockingQueue<java.lang.String> urlListQueue, BlockingQueue<String> urlQueue) {
        this.urlListQueue = urlListQueue;
        this.urlQueue = urlQueue;
    }

    @Override
    public Collection<String> process(String url) throws IOException {

        log.info("get-url:" + Const.LIB_URL + url);
        // 解析详情页url
        Document doc = JsoupUtils.doGet(Const.LIB_URL + url);

        // 获取所有链接
        Elements links = doc.select("a[href]");



        for (Element element : links) {
            if ("下一页".equals(element.text())) {
                try {
                    urlListQueue.put(element.attr("href").substring(4));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        // 详情页地址
        return links.stream()
                .filter(link -> Pattern.matches(".*v=.*", link.attr("href")))
                .map(link -> link.attr("href").substring(2))
                .collect(Collectors.toList());
    }


    /**
     * 队列有值时调用，空队列阻塞
     */
    public void onTask() {
        new Thread(() -> {
            while (true) {
                String listUrl = null;
                try {
                    listUrl = urlListQueue.take();

                    Collection<String> process = null;
                    try {
                        process = this.process(listUrl);
                    } catch (IOException e) {
//                        log.error(Const.LIB_URL + listUrl, e);
                        urlListQueue.put(listUrl);
                    }
                    if (Objects.nonNull(process)) {
                        for (String url : process) {
                            urlQueue.put(url);
                        }
                    }
                } catch (InterruptedException e) {
                    log.error(e.toString(), e);
                }
            }
        }).start();
    }

}
