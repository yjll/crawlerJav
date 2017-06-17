package service;

import dao.SessionFactory;
import dao.VideoActorDao;
import dao.VideoCategoryDao;
import dao.VideoInfoDao;
import dto.LibWebInfo;
import dto.VideoActor;
import dto.VideoCategory;
import dto.VideoInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.session.SqlSession;
import util.CommonUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static util.Count.LIB_WEB_INFO_SET_PATH;

public class LibWebService {

    SqlSession sqlSession = SessionFactory.newSqlSession();

    VideoInfoDao videoInfoDao = sqlSession.getMapper(VideoInfoDao.class);

    VideoCategoryDao videoCategoryDao = sqlSession.getMapper(VideoCategoryDao.class);

    VideoActorDao videoActorDao = sqlSession.getMapper(VideoActorDao.class);


    public void blMain(Set<LibWebInfo> libWebInfoSet) throws InvocationTargetException, IllegalAccessException, SQLException {

        Set<String> tempSet = new HashSet<>();
        // 本地影片信息
        Set<LibWebInfo> localLibWebInfoList = (Set<LibWebInfo>) CommonUtil.getObject(LIB_WEB_INFO_SET_PATH);
        // 影片表
        List<VideoInfo> videoInfoList = new ArrayList<>();
        // 演员表
        List<VideoActor> videoActorList = new ArrayList<>();
        // 类别表
        List<VideoCategory> videoCategoryList = new ArrayList<>();

        tempSet.addAll(localLibWebInfoList.stream().map(LibWebInfo::getNumber).collect(Collectors.toList()));

        for (LibWebInfo libWebInfo : libWebInfoSet) {
            // 去掉重复影片
            if (tempSet.add(libWebInfo.getNumber())) {
                VideoInfo videoInfo = new VideoInfo();
                BeanUtils.copyProperties(videoInfo, libWebInfo);
                videoInfoList.add(videoInfo);

                for (String actor : libWebInfo.getActorList()) {
                    VideoActor videoActor = new VideoActor();
                    videoActor.setNo(libWebInfo.getNumber());
                    videoActor.setActor(actor);
                    videoActorList.add(videoActor);
                }

                for (String category : libWebInfo.getCategoryList()) {
                    VideoCategory videoCategory = new VideoCategory();
                    videoCategory.setNo(libWebInfo.getNumber());
                    videoCategory.setCategory(category);
                    videoCategoryList.add(videoCategory);
                }
            }
        }
        try {
            videoInfoList.forEach(videoInfoDao::insert);
            videoActorList.forEach(videoActorDao::insert);
            videoCategoryList.forEach(videoCategoryDao::insert);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException();
        }

        System.out.println("数据更新成功...");

    }
}
