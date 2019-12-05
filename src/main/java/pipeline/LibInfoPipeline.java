package pipeline;

import config.ModelMapstruct;
import dao.VideoActorMapper;
import dao.VideoCategoryMapper;
import dao.VideoInfoMapper;
import dto.LibWebInfo;
import lombok.extern.slf4j.Slf4j;
import model.VideoActor;
import model.VideoCategory;
import model.VideoInfo;
import org.mybatis.guice.transactional.Transactional;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Singleton
@Slf4j
public class LibInfoPipeline {

    @Inject
    private ModelMapstruct modelMapstruct;

    @Inject
    private VideoInfoMapper videoInfoDao;
    @Inject
    private VideoCategoryMapper videoCategoryDao;
    @Inject
    private VideoActorMapper videoActorDao;


    public void save(Collection<LibWebInfo> libWebInfoSet){

        libWebInfoSet.forEach(this::saveVideoInfo);
        log.info("数据更新成功...");
    }

    @Transactional
    public void saveVideoInfo(LibWebInfo libWebInfo) {
        // 根据no判断在否存在
        VideoInfo dbVideo = videoInfoDao.selectByPrimaryKey(libWebInfo.getNo());

        if (Objects.isNull(dbVideo)) {
            VideoInfo videoInfo = modelMapstruct.libWebInfoToVideoInfo(libWebInfo);
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
            VideoInfo videoInfo = modelMapstruct.libWebInfoToVideoInfo(libWebInfo);
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
        LibWebInfo libWebInfo = modelMapstruct.videoInfoToLibWebInfo(videoInfo);
        List<String> videoCategoryList = videoCategoryDao.findVideoCategory(no);
        List<String> videoActorList = videoActorDao.findVideoActor(no);
        libWebInfo.setActorList(videoActorList);
        libWebInfo.setCategoryList(videoCategoryList);
        return libWebInfo;
    }

}

