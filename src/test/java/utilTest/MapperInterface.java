package utilTest;

import dto.LibWebInfo;
import dto.VideoInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author: zijing
 * @date: 2018/6/5 20:28
 * @description:
 */
@Mapper
public interface MapperInterface {

    MapperInterface INTERFACE = Mappers.getMapper(MapperInterface.class);

    VideoInfo map( LibWebInfo libWebInfo);


}
