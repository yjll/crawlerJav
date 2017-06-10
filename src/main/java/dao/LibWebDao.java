package dao;

import dto.VideoActor;
import dto.VideoCategory;
import dto.VideoInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface LibWebDao {
    /**
     * 获取所有番号列表
     *
     * @return
     */
    List<String> findNoList();
}
