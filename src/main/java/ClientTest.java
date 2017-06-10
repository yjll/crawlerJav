import dao.LibWebDao;
import dao.SessionFactory;
import dto.LibWebInfo;
import org.apache.ibatis.session.SqlSession;
import service.LibWebService;
import util.CommonUtil;
import util.PropertyUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class ClientTest {

    public static void main(String[] args) {
        SqlSession sqlSession = SessionFactory.newSqlSession();
//        LibWebDao libWebDao = sqlSession.getMapper(LibWebDao.class);

        List<String> noList = sqlSession.selectList("findNoList");
        try {
//            noList = libWebDao.findNoList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        noList.forEach(System.out::println);
    }

}
