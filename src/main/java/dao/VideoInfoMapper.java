package dao;

import model.VideoInfo;

import java.util.List;

public interface VideoInfoMapper {
    int deleteByPrimaryKey(String no);

    int insert(VideoInfo record);

    int insertSelective(VideoInfo record);

    VideoInfo selectByPrimaryKey(String no);

    int updateByPrimaryKeySelective(VideoInfo record);

    int updateByPrimaryKey(VideoInfo record);

    List<VideoInfo> findAll();

}