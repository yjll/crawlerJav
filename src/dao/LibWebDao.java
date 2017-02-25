package dao;

import dto.VideoActor;
import dto.VideoCategory;
import dto.VideoInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LibWebDao {

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jav?useUnicode=true&characterEncoding=UTF-8", "root", "admin");
        conn.setAutoCommit(false);
        return conn;
    }

    public void insertVideoInfo(Connection conn, List<VideoInfo> videoInfoList) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO video_info(video_no,video_title,video_date,video_duration,video_rated,system_time)VALUES(?,?,?,?,?,CURDATE())");
        try {
            for (VideoInfo videoInfo : videoInfoList) {
                stmt.setString(1, videoInfo.getNumber());
                stmt.setString(2, videoInfo.getTile());
                stmt.setString(3, videoInfo.getDate());
                stmt.setString(4, videoInfo.getDuration());
                stmt.setString(5, videoInfo.getRated());
                stmt.executeUpdate();
            }
        } finally {
            stmt.close();
        }
    }

    public void insertVideoActor(Connection conn, List<VideoActor> videoActorList) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO video_actor(video_no,video_actor)VALUES(?,?)");
        try {
            for (VideoActor videoActor : videoActorList) {
                stmt.setString(1, videoActor.getNumber());
                stmt.setString(2, videoActor.getActor());
                stmt.executeUpdate();
            }
        } finally {
            stmt.close();
        }
    }

    public void insertVideoCategory(Connection conn, List<VideoCategory> videoCategoryList) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO video_category(video_no,video_category)VALUES(?,?)");
        try {
            for (VideoCategory videoCategoryBase : videoCategoryList) {
                stmt.setString(1, videoCategoryBase.getNumber());
                stmt.setString(2, videoCategoryBase.getCategory());
                stmt.executeUpdate();
            }
        } finally {
            stmt.close();
        }
    }
}
