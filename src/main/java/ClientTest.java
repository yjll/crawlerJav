import dao.SessionFactory;
import dao.VideoInfoDao;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class ClientTest {

    public static void main(String[] args) {
        SqlSession sqlSession = SessionFactory.newSqlSession();
        VideoInfoDao videoInfoDao = sqlSession.getMapper(VideoInfoDao.class);
//        LibWebDao libWebDao = sqlSession.getMapper(LibWebDao.class);

        List<String> noList = null;
//                = sqlSession.selectList("findNoList");
        try {
            noList = videoInfoDao.findNoList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        noList.forEach(System.out::println);
    }

}
