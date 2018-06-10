package dao;

import model.VideoCategory;

import java.util.List;

public interface VideoCategoryMapper {
    int insert(VideoCategory record);

    int insertSelective(VideoCategory record);

    List<String> findVideoCategory(String no);
}