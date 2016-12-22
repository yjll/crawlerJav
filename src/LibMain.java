import Action.LibWebConnect;
import dto.LibWebInfo;
import util.CommonUtil;
import util.PropertyUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class LibMain {
    public static void main(String[] args) {
        // 从Web获取链接列表
        Set<String> webLibUrlSet = LibWebConnect.getLibUrlSet();
        // 本地链接列表
        Set<String> localLibUrlSet = new HashSet<>();
        // 最近更新的链接列表
        Set<String> newLibUrlSet = new HashSet<>();
        File file = new File(PropertyUtil.getProperty("LIB_URL_SET_PATH"));
        if (!file.exists()) {
            // 第一次获取链接
            newLibUrlSet = webLibUrlSet;
        } else {
            localLibUrlSet = (Set<String>) CommonUtil.getObject("LIB_URL_SET_PATH");
            for (String url : webLibUrlSet) {
                if (!localLibUrlSet.contains(url)) {
                    newLibUrlSet.add(url);
                }
            }
        }
        System.out.println("The lengeth of newLibUrlSet is " + newLibUrlSet.size());
        // 获取页面信息
        Set<LibWebInfo> libWebInfoSet = LibWebConnect.getLibWebInfoSet(newLibUrlSet);
        for (LibWebInfo libWebInfo : libWebInfoSet) {
            // 已获取到的页面的地址
            localLibUrlSet.add(libWebInfo.getUrl());
        }
        CommonUtil.saveObject(localLibUrlSet, "LIB_URL_SET_PATH");
        CommonUtil.saveObject(libWebInfoSet, "LIB_WEB_INFO_SET_PATH");
        System.out.println("===End===");
    }
}
