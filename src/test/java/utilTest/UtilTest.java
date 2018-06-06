package utilTest;

import dao.SessionFactory;
import dao.VideoInfoDao;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

import static util.Const.IMAGE_ROOT_PATH;

public class UtilTest {

    @Test
    public void getPropertyTest(){
        SqlSession sqlSession = SessionFactory.newSqlSession();

        VideoInfoDao videoInfoDao = sqlSession.getMapper(VideoInfoDao.class);

        List<String> noList = videoInfoDao.findNoList();
        System.out.println(noList.get(0));
    }

    @Test
    public void fileTest(){

//        File imageFile = new File(IMAGE_ROOT_PATH + libWebInfo.getNo() + ".jpg");
//        Assert.assertTrue(file.exists());
        System.out.println(IMAGE_ROOT_PATH);

    }
}
