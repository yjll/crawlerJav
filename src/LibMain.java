import dto.LibWebInfo;
import service.LibWebService;
import util.CommonUtil;
import util.LibWebConnect;
import util.PropertyUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LibMain {

    // Lib链接本地存储路径
    public static final String LIB_URL_SET_PATH = PropertyUtil.getProperty("LIB_URL_SET_PATH");
    // Lib网页信息本地存储路径
    public static final String LIB_WEB_INFO_SET_PATH = PropertyUtil.getProperty("LIB_WEB_INFO_SET_PATH");

    public static void main(String[] args) throws Exception {
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
                    System.out.println("更新链接" + url);
                    newLibUrlSet.add(url);
                }
            }
        }

        LibWebService libWebService = new LibWebService();

        Set<LibWebInfo> dbSet = libWebConnect.getDbSet(newLibUrlSet);
        // 将数据存入数据库中
        libWebService.blMain(dbSet);

        // 本地Lib网页信息
        Set<LibWebInfo> localLibWebInfoSet = (Set<LibWebInfo>) CommonUtil.getObject(LIB_WEB_INFO_SET_PATH);
        localLibWebInfoSet.addAll(dbSet);

        localLibUrlSet.addAll(dbSet.stream().map(libWebInfo -> libWebInfo.getUrl()).collect(Collectors.toSet()));

        CommonUtil.saveObject(localLibUrlSet, LIB_URL_SET_PATH);
        CommonUtil.saveObject(localLibWebInfoSet, LIB_WEB_INFO_SET_PATH);
        System.out.println("===End===");
    }
}
