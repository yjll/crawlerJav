package utilTest;

import com.google.inject.Guice;
import org.junit.Test;
import run.LibMain;
import config.BindConfig;

public class UtilTest {


    @Test
    public void fileTest() {

//        File imageFile = new File(IMAGE_ROOT_PATH + libWebInfo.getNo() + ".jpg");
//        Assert.assertTrue(file.exists());
//        System.out.println(IMAGE_ROOT_PATH);
        LibMain libMain = Guice.createInjector(new BindConfig()).getInstance(LibMain.class);
        System.out.println(libMain);
//        libMain

    }
}
