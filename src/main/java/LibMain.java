import dto.LibWebInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pipeline.LibWebService;
import service.LibWebConnect;
import util.CommonUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static util.Count.LIB_URL_SET_PATH;

public class LibMain {

    static Log logger = LogFactory.getLog(LibMain.class);

    public static void main(String[] args) throws Exception {
        logger.info("===Start===");
        // 链接列表
        Set<String> webLibUrlSet;
        // 本地链接列表
        Set<String> localLibUrlSet = new HashSet<>();
        // 最近更新的链接列表
        Set<String> newLibUrlSet = new HashSet<>();


        LibWebConnect libWebConnect = new LibWebConnect();
        // 从Web获取链接列表
        webLibUrlSet = libWebConnect.getTopLibUrlSet();

        File file = new File(LIB_URL_SET_PATH);
        if (!file.exists()) {
            // 第一次获取链接
            newLibUrlSet = webLibUrlSet;
        } else {
            localLibUrlSet = (Set<String>) CommonUtil.getObject(LIB_URL_SET_PATH);
            for (String url : webLibUrlSet) {
                if (!localLibUrlSet.contains(url)) {
                    logger.info("更新链接" + url);
                    newLibUrlSet.add(url);
                }
            }
        }

        LibWebService libWebService = new LibWebService();

        Set<LibWebInfo> dbSet = libWebConnect.getDbSet(newLibUrlSet);
        // 将数据存入数据库中
        libWebService.blMain(dbSet);

        localLibUrlSet.addAll(dbSet.stream().map(LibWebInfo::getUrl).collect(Collectors.toSet()));

        CommonUtil.saveObject(localLibUrlSet, LIB_URL_SET_PATH);

        logger.info("===End===");
    }
}
