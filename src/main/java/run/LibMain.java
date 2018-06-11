package run;

import dto.LibWebInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pipeline.LibInfoPipeline;
import processor.LibWebProcessor;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Set;

public class LibMain {

    @Inject
    LibWebProcessor libWebProcessor;

    @Inject
    LibInfoPipeline libInfoPipeline;

    static Log logger = LogFactory.getLog(LibMain.class);

    public static void main(String[] args) throws Exception {
        logger.info("===Start===");
        // 链接列表
        LibMain libMain = new LibMain();
        libMain.saveTop();


        logger.info("===End===");
    }

    private  void saveTop() throws InvocationTargetException, IllegalAccessException, SQLException {
        // 从Web获取链接列表
        Set<String> webLibUrlSet = libWebProcessor.getTopLibUrlSet();

        Set<LibWebInfo> dbSet = libWebProcessor.getDbSet(webLibUrlSet);
        // 将数据存入数据库中
        libInfoPipeline.save(dbSet);
    }
}
