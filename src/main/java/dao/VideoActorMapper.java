package dao;

import model.VideoActor;

import java.util.List;

public interface VideoActorMapper {
    int insert(VideoActor record);

    int insertSelective(VideoActor record);

    List<String> findVideoActor(String no);
}