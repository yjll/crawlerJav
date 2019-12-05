package run;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import config.BindConfig;
import config.MybatisConfig;
import dto.LibWebInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.guice.datasource.helper.JdbcHelper;
import pipeline.LibInfoMarkdownPipeline;
import pipeline.LibInfoPipeline;
import processor.LibWebInfoProcessor;
import processor.LibWebUrlProcessor;
import util.Const;
import util.PropertyUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.Binder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Singleton
public class LibMain {

    @Inject
    LibInfoPipeline libInfoPipeline;

    @Inject
    LibInfoMarkdownPipeline libInfoMarkdownPipeline;


    public static void main(String[] args) throws Exception {
        log.info("===Start===");

        Injector injector = Guice.createInjector(new BindConfig(),
                new MybatisConfig());

        String indexUrl = Const.INDEX_URL.replace(Const.LIB_URL, "");

        LibMain instance = injector.getInstance(LibMain.class);
        instance.run(indexUrl);

        log.info("===End===");
    }

    private void run(String url) throws InterruptedException {
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
            if (urlListQueue.isEmpty() & urlQueue.isEmpty() & libWebInfoProcessor.isFinished()) {

                String md = libInfoMarkdownPipeline.process(libWebInfoResult);
                try {
                    Files.write(new File(StringUtils.isNotBlank(Const.ACTOR) ? Const.ACTOR + ".md" : "lib.md").toPath(), md.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 将数据存入数据库中
                libInfoPipeline.save(libWebInfoResult);

                break;
            }
            log.info("任务未完成...");
        }
    }
}
