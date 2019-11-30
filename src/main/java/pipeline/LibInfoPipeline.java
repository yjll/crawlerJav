package pipeline;

import config.ModelMapper;
import dao.VideoActorMapper;
import dao.VideoCategoryMapper;
import dao.VideoInfoMapper;
import dto.LibWebInfo;
import lombok.extern.slf4j.Slf4j;
import model.VideoActor;
import model.VideoCategory;
import model.VideoInfo;
import org.apache.ibatis.session.SqlSession;
import util.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class LibInfoPipeline {

    @Inject
    private ModelMapper modelMapper;

    SqlSession sqlSession = SessionFactory.newSqlSession();

    private VideoInfoMapper videoInfoDao = sqlSession.getMapper(VideoInfoMapper.class);

    private VideoCategoryMapper videoCategoryDao = sqlSession.getMapper(VideoCategoryMapper.class);

    private VideoActorMapper videoActorDao = sqlSession.getMapper(VideoActorMapper.class);


    public void save(Collection<LibWebInfo> libWebInfoSet){

        // 取全部影片信息
        List<VideoInfo> allVideo = videoInfoDao.findAll();
        Map<String, VideoInfo> videoInfoMap = allVideo.stream().collect(Collectors.toMap(VideoInfo::getNo, Function.identity()));


        for (LibWebInfo libWebInfo : libWebInfoSet) {
            try {
                saveVideoInfo(videoInfoMap, libWebInfo);

                sqlSession.commit();
            } catch (Exception e) {
                sqlSession.rollback();
                log.error(e.toString(), e);
            }
        }

        log.info("数据更新成功...");
    }

    private void saveVideoInfo(Map<String, VideoInfo> videoInfoMap, LibWebInfo libWebInfo) {
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
            videoInfoDao.updateByPrimaryKeySelective(videoInfo);
        }
    }

    /**
     * 根据No获取影片信息
     * @param no
     * @return
     */
    public LibWebInfo getLibWebInfo(String no){

        VideoInfo videoInfo = videoInfoDao.selectByPrimaryKey(no);
        LibWebInfo libWebInfo = modelMapper.videoInfoToLibWebInfo(videoInfo);
        List<String> videoCategoryList = videoCategoryDao.findVideoCategory(no);
        List<String> videoActorList = videoActorDao.findVideoActor(no);
        libWebInfo.setActorList(videoActorList);
        libWebInfo.setCategoryList(videoCategoryList);
        return libWebInfo;
    }

}

