import Action.LibWebConnect;
import dto.LibWebInfo;
import util.CommonUtil;
import util.PropertyUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LibMain {
    public static void main(String[] args) {
        // Lib网站
        LibWebConnect libWebConnect = new LibWebConnect();
        // 获取链接列表
        Set<String> webLibUrlSet = libWebConnect.getLibUrlSet();
        // 本地链接列表
        Set<String> localLibUrlSet = null;
        // 最近更新的链接列表
        Set<String> newLibUrlSet = new HashSet<>();
        File file = new File(PropertyUtil.getProperty("LIB_URL_SET_PATH"));
        if (!file.exists()) {
            // 第一次获取链接
            localLibUrlSet = webLibUrlSet;
            newLibUrlSet = webLibUrlSet;
        } else {
            localLibUrlSet = (Set<String>) CommonUtil.getObject("LIB_URL_SET_PATH");
            for (String url : webLibUrlSet) {
                if (localLibUrlSet.add(url)) {
                    newLibUrlSet.add(url);
                }
            }
        }
        CommonUtil.saveObject((HashSet) localLibUrlSet, "LIB_URL_SET_PATH");
        System.out.println("====End====");
        System.out.println("Set长度为: " + newLibUrlSet.size());

        // 单线程
        long start = System.currentTimeMillis();
        List<LibWebInfo> list = new ArrayList<>();
        for(String libUrl: newLibUrlSet){
            list.add(libWebConnect.analysis(libUrl));
        }
        long end = System.currentTimeMillis();
        System.out.println("单多线程运行时间：" + (end - start) + "ms");
        System.out.println("单线程列表长度：" + list.size() );

    }
}
