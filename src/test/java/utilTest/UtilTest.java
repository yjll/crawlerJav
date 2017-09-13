package utilTest;

import dao.SessionFactory;
import dao.VideoInfoDao;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import util.Const;

import java.util.List;

public class UtilTest {

    @Test
    public void getPropertyTest(){
        System.out.println(Const.LIB_URL_SET_PATH);
    }
}
