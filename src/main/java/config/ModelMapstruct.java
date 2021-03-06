package config;

import dto.LibWebInfo;
import model.VideoInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author: zijing
 * @date: 2018/6/5 20:28
 * @description:
 */
@Mapper()
public interface ModelMapstruct {

    @Mapping(target = "image",source = "imageUrl")
    VideoInfo libWebInfoToVideoInfo(LibWebInfo libWebInfo);

    LibWebInfo videoInfoToLibWebInfo(VideoInfo videoInfo);

}
