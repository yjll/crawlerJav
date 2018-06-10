package pipeline;

import dao.VideoActorMapper;
import dao.VideoCategoryMapper;
import dao.VideoInfoMapper;
import dto.LibWebInfo;
import model.VideoActor;
import model.VideoCategory;
import model.VideoInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import config.ModelMapper;
import util.SessionFactory;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LibInfoService {

    private Log logger = LogFactory.getLog(this.getClass());

    @Inject
    private ModelMapper modelMapper;

    SqlSession sqlSession = SessionFactory.newSqlSession();

    private VideoInfoMapper videoInfoDao = sqlSession.getMapper(VideoInfoMapper.class);

    private VideoCategoryMapper videoCategoryDao = sqlSession.getMapper(VideoCategoryMapper.class);

    private VideoActorMapper videoActorDao = sqlSession.getMapper(VideoActorMapper.class);


    public void blMain(Set<LibWebInfo> libWebInfoSet) throws InvocationTargetException, IllegalAccessException, SQLException {

        // 取全部影片信息
        List<VideoInfo> allVideo = videoInfoDao.findAll();
        Map<String, VideoInfo> videoInfoMap = allVideo.stream().collect(Collectors.toMap(VideoInfo::getNo, Function.identity()));


        for (LibWebInfo libWebInfo : libWebInfoSet) {
            try {
                saveWebInfo(videoInfoMap, libWebInfo);

                sqlSession.commit();
            } catch (Exception e) {
                sqlSession.rollback();
                logger.error(e.toString(), e);
            }
        }

        logger.info("数据更新成功...");
    }

    private void saveWebInfo(Map<String, VideoInfo> videoInfoMap, LibWebInfo libWebInfo) {
        // 根据no判断在否存在
        VideoInfo dbVideo = videoInfoMap.get(libWebInfo.getNo());

        if (Objects.isNull(dbVideo)) {
            VideoInfo videoInfo = modelMapper.libWebInfoToVideoInfo(libWebInfo);
            videoInfoDao.insertSelective(videoInfo);

            for (String actor : libWebInfo.getActorList()) {
                VideoActor videoActor = new VideoActor();
                videoActor.setNo(libWebInfo.getNo());
                videoActor.setActor(actor);
                videoActorDao.insertSelective(videoActor);
            }

            for (String category : libWebInfo.getCategoryList()) {
                VideoCategory videoCategory = new VideoCategory();
                videoCategory.setNo(libWebInfo.getNo());
                videoCategory.setCategory(category);
                videoCategoryDao.insertSelective(videoCategory);
            }
        } else {
            VideoInfo videoInfo = modelMapper.libWebInfoToVideoInfo(libWebInfo);
            videoInfo.setSystemTime(dbVideo.getSystemTime());
            videoInfoDao.updateByPrimaryKeySelective(videoInfo);
        }
    }

    public LibWebInfo getLibWebInfo(String no){

        VideoInfo videoInfo = videoInfoDao.selectByPrimaryKey(no);
        LibWebInfo libWebInfo = modelMapper.videoInfoToLibWebInfo(videoInfo);
        List<String> videoCategoryList = videoCategoryDao.findVideoCategory(no);
        List<String> videoActorList = videoActorDao.findVideoActor(no);
        libWebInfo.setActorList(videoActorList);
        libWebInfo.setCategoryList(videoCategoryList);
        System.out.println(libWebInfo);


//        List<VideoActor> videoActors = modelMapper.libWebInfoToVideoActor(libWebInfo);
//        System.out.println(videoActors);
        return libWebInfo;
    }

}

