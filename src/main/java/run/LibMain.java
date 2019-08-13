package run;

import com.google.inject.Guice;
import com.google.inject.Injector;
import config.BindConfig;
import dto.LibWebInfo;
import lombok.extern.slf4j.Slf4j;
import pipeline.LibInfoMarkdownPipeline;
import pipeline.LibInfoPipeline;
import processor.LibWebInfoProcessor;
import processor.LibWebUrlProcessor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

@Slf4j
@Singleton
public class LibMain {

    @Inject
    LibInfoPipeline libInfoPipeline;

    @Inject
    LibInfoMarkdownPipeline libInfoMarkdownPipeline;

    public static void main(String[] args) throws Exception {
        log.info("===Start===");

        String indexUrl = "";
        Predicate<LibWebInfo> predicate = libWebInfo -> libWebInfo.getTitle().contains("");

        Injector injector = Guice.createInjector(new BindConfig());
        LibMain instance = injector.getInstance(LibMain.class);
        instance.run(indexUrl,predicate);

        log.info("===End===");
    }

    private void run(String url,Predicate<LibWebInfo> predicate) throws InterruptedException {
        BlockingQueue<String> urlListQueue = new ArrayBlockingQueue<>(1024);
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(1024);
        // 爬虫起点
        urlListQueue.put(url);
        // 爬取列表
        LibWebUrlProcessor libWebUrlProcessor = new LibWebUrlProcessor(urlListQueue, urlQueue);
        // 爬取结果
        CopyOnWriteArrayList<LibWebInfo> libWebInfoResult = new CopyOnWriteArrayList<>();
        // 爬取目标信息
        LibWebInfoProcessor libWebInfoProcessor = new LibWebInfoProcessor(urlQueue, libWebInfoResult);

        // 爬取详情页,获取url
        libWebUrlProcessor.onTask();

        // 监听队列,爬取详情信息
        libWebInfoProcessor.onTask();

        while (true) {
            // 让子弹飞一会
            Thread.sleep(10_000L);
            // 任务已完成
            if (urlListQueue.isEmpty() && urlQueue.isEmpty() && libWebInfoProcessor.isFinished()) {

                libInfoMarkdownPipeline.process(libWebInfoResult,predicate);

                // 将数据存入数据库中
                libInfoPipeline.save(libWebInfoResult);

                break;
            }
            log.info("任务未完成...");
        }
    }
}
