package dao;

import dto.VideoActorBase;
import dto.VideoCategory;
import dto.VideoInfoBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LibWebDao {

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.56.10:1521:orcl", "scott", "root");
        conn.setAutoCommit(false);
        return conn;
    }

    public void insertVideoInfo(Connection conn, List<VideoInfoBase> videoInfoBaseList) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO video_info(video_no,video_title,video_date,video_duration,video_rated)VALUES(?,?,TO_DATE(?,'YYYY-MM-DD'),?,?)");
        try {
            for (VideoInfoBase videoInfoBase : videoInfoBaseList) {
                stmt.setString(1, videoInfoBase.getNumber());
                stmt.setString(2, videoInfoBase.getTile());
                stmt.setString(3, videoInfoBase.getDate());
                stmt.setString(4, videoInfoBase.getDuration());
                stmt.setString(5, videoInfoBase.getRated());
                stmt.executeUpdate();
            }
        } finally {
            stmt.close();
        }
    }

    public void insertVideoActor(Connection conn, List<VideoActorBase> videoActorBaseList) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO video_actor(video_no,video_actor)VALUES(?,?)");
        try {
            for (VideoActorBase videoActorBase : videoActorBaseList) {
                stmt.setString(1, videoActorBase.getNumber());
                stmt.setString(2, videoActorBase.getActor());
                stmt.executeUpdate();
            }
        } finally {
            stmt.close();
        }
    }

    public void insertVideoCategory(Connection conn, List<VideoCategory> videoCategoryList) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INT video_category(video_no,video_category)VALUES(?,?)");
        try {
            for (VideoCategory videoCategoryBase : videoCategoryList) {
                stmt.setString(1, videoCategoryBase.getNumber());
                stmt.setString(2, videoCategoryBase.getCategory());
                stmt.executeUpdate();
            }
        }finally {
        stmt.close();
        }
    }
}
