package dao;


import dto.VideoInfo;

import java.util.List;

public interface VideoInfoDao {
    int insert(VideoInfo record);

    VideoInfo selectByNo(String no);

    List<String> findNoList();

}