import dto.LibWebInfo;
import service.LibWebService;
import util.CommonUtil;
import util.LibWebConnect;
import util.PropertyUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class LibMain {

    // Lib链接本地存储路径
    public static String libUrlSetPath = PropertyUtil.getProperty("LIB_URL_SET_PATH");
    // Lib网页信息本地存储路径
    public static String libWebInfoSetPath = PropertyUtil.getProperty("LIB_WEB_INFO_SET_PATH");

    public static void main(String[] args) throws Exception {
        // 链接列表
        Set<String> webLibUrlSet;

        LibWebConnect libWebConnect = new LibWebConnect();
        while (true) {
            libWebConnect.failLibSet.clear();
            // 从Web获取链接列表
            webLibUrlSet = libWebConnect.getTopLibUrlSet();
            if (libWebConnect.failLibSet.size() == 0) {
                break;
            }
        }
        // 本地链接列表
        Set<String> localLibUrlSet = new HashSet<>();
        // 最近更新的链接列表
        Set<String> newLibUrlSet = new HashSet<>();
        // 本地Lib网页信息
        Set<LibWebInfo> localLibWebInfoSet = new HashSet<>();
        File file = new File(libUrlSetPath);
        if (!file.exists()) {
            // 第一次获取链接
            newLibUrlSet = webLibUrlSet;
        } else {
            localLibUrlSet = (Set<String>) CommonUtil.getObject(libUrlSetPath);
            localLibWebInfoSet = (Set<LibWebInfo>) CommonUtil.getObject(libWebInfoSetPath);
            for (String url : webLibUrlSet) {
                if (!localLibUrlSet.contains(url)) {
                    System.out.println("更新链接" + url);
                    newLibUrlSet.add(url);
                }
            }
        }
        // 页面信息
        Set<LibWebInfo> libWebInfoSet;
        // 插入数据库数据
        Set<LibWebInfo> dbSet = new HashSet<>();
        while (true) {
            System.out.println("The size of newLibUrlSet is " + newLibUrlSet.size());
            libWebConnect.failLibSet.clear();
            // 获取页面信息
            libWebInfoSet = libWebConnect.getLibWebInfoSet(newLibUrlSet);

            for (LibWebInfo libWebInfo : libWebInfoSet) {
                // 已获取到的页面的地址
                localLibUrlSet.add(libWebInfo.getUrl());
                localLibWebInfoSet.add(libWebInfo);
                dbSet.add(libWebInfo);
            }
            // 获取失败链接
            Set<String> failLibSet = new HashSet<>();
            for (String url : libWebConnect.failLibSet) {
                failLibSet.add(url);
            }
            if (failLibSet.size() == 0) {
                break;
            } else {
                System.out.println("超时URL:");
                for (String str : failLibSet) {
                    System.out.println(str);
                }
            }
            // 根据失败的链接重新获取信息
            newLibUrlSet = failLibSet;
        }
        LibWebService libWebService = new LibWebService();
        // 将数据存入数据库中
        libWebService.blMain(dbSet);

        CommonUtil.saveObject(localLibUrlSet, libUrlSetPath);
        CommonUtil.saveObject(localLibWebInfoSet, libWebInfoSetPath);
        System.out.println("===End===");
    }
}
