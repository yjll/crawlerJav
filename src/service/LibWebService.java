package service;

import dao.LibWebDao;
import dto.LibWebInfo;
import dto.VideoActorBase;
import dto.VideoCategory;
import dto.VideoInfoBase;
import org.apache.commons.beanutils.BeanUtils;
import util.CommonUtil;
import util.PropertyUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LibWebService {
    // Lib网页信息本地存储路径
    public static String libWebInfoSetPath = PropertyUtil.getProperty("LIB_WEB_INFO_SET_PATH");

    public void blMain(Set<LibWebInfo> libWebInfoSet) throws InvocationTargetException, IllegalAccessException, SQLException {

        Set<String> tempSet = new HashSet<>();
        // 本地影片信息
        Set<LibWebInfo> localLibWebInfoList = (Set<LibWebInfo>) CommonUtil.getObject(libWebInfoSetPath);
        // 影片表
        List<VideoInfoBase> videoInfoBaseList = new ArrayList<>();
        // 演员表
        List<VideoActorBase> videoActorBasesList = new ArrayList<>();
        // 类别表
        List<VideoCategory> videoCategoryList = new ArrayList<>();

        for(LibWebInfo libWebInfo : localLibWebInfoList){
            tempSet.add(libWebInfo.getNumber());
        }

        for (LibWebInfo libWebInfo : libWebInfoSet) {
            // 去掉重复影片
            if (tempSet.add(libWebInfo.getNumber())) {
                VideoInfoBase videoInfoBase = new VideoInfoBase();
                BeanUtils.copyProperties(videoInfoBase, libWebInfo);
                videoInfoBaseList.add(videoInfoBase);

                for (String actor : libWebInfo.getActorList()) {
                    VideoActorBase videoActorBase = new VideoActorBase();
                    videoActorBase.setNumber(libWebInfo.getNumber());
                    videoActorBase.setActor(actor);
                    videoActorBasesList.add(videoActorBase);
                }

                for (String category : libWebInfo.getCategoryList()) {
                    VideoCategory videoCategory = new VideoCategory();
                    videoCategory.setNumber(libWebInfo.getNumber());
                    videoCategory.setCategory(category);
                    videoCategoryList.add(videoCategory);
                }
            }
        }
        LibWebDao libWebDao = new LibWebDao();
        Connection conn = null;
        try {
            conn = libWebDao.getConnection();
            libWebDao.insertVideoInfo(conn, videoInfoBaseList);
            libWebDao.insertVideoActor(conn, videoActorBasesList);
            libWebDao.insertVideoCategory(conn, videoCategoryList);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new SQLException();
        } finally {
            conn.close();
        }

        System.out.println("数据更新成功...");

    }
}
