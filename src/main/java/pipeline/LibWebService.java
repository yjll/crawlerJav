package pipeline;

import dao.SessionFactory;
import dao.VideoActorDao;
import dao.VideoCategoryDao;
import dao.VideoInfoDao;
import dto.LibWebInfo;
import dto.VideoActor;
import dto.VideoCategory;
import dto.VideoInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LibWebService {

    private Log logger = LogFactory.getLog(this.getClass());

    SqlSession sqlSession = SessionFactory.newSqlSession();

    private VideoInfoDao videoInfoDao = sqlSession.getMapper(VideoInfoDao.class);

    private VideoCategoryDao videoCategoryDao = sqlSession.getMapper(VideoCategoryDao.class);

    private VideoActorDao videoActorDao = sqlSession.getMapper(VideoActorDao.class);


    public void blMain(Set<LibWebInfo> libWebInfoSet) throws InvocationTargetException, IllegalAccessException, SQLException {

        // 影片表
        List<VideoInfo> videoInfoList = new ArrayList<>();
        // 演员表
        List<VideoActor> videoActorList = new ArrayList<>();
        // 类别表
        List<VideoCategory> videoCategoryList = new ArrayList<>();

        // 获取所有影片番号
        List<String> noList = videoInfoDao.findNoList();

        for (LibWebInfo libWebInfo : libWebInfoSet) {
            // 去掉重复影片
            if (!noList.contains(libWebInfo.getNo())) {
                VideoInfo videoInfo = new VideoInfo();
                BeanUtils.copyProperties(videoInfo, libWebInfo);
                videoInfoList.add(videoInfo);

                for (String actor : libWebInfo.getActorList()) {
                    VideoActor videoActor = new VideoActor();
                    videoActor.setNo(libWebInfo.getNo());
                    videoActor.setActor(actor);
                    videoActorList.add(videoActor);
                }

                for (String category : libWebInfo.getCategoryList()) {
                    VideoCategory videoCategory = new VideoCategory();
                    videoCategory.setNo(libWebInfo.getNo());
                    videoCategory.setCategory(category);
                    videoCategoryList.add(videoCategory);
                }
            }
        }

        try {
            videoInfoList.forEach(videoInfoDao::insert);
            videoActorList.forEach(videoActorDao::insert);
            videoCategoryList.forEach(videoCategoryDao::insert);
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
            throw new SQLException();
        }

        logger.info("数据更新成功...");
    }

}

