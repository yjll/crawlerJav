package config;

import dto.LibWebInfo;
import model.VideoInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author: zijing
 * @date: 2018/6/5 20:28
 * @description:
 */
@Mapper()
public interface ModelMapper {

    VideoInfo libWebInfoToVideoInfo(LibWebInfo libWebInfo);

    LibWebInfo videoInfoToLibWebInfo(VideoInfo videoInfo);

}
