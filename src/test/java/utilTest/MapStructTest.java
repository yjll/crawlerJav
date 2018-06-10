package utilTest;

import com.google.inject.Guice;
import config.BindConfig;
import dto.LibWebInfo;
import org.junit.Test;
import pipeline.LibInfoService;

/**
 * @author: zijing
 * @date: 2018/6/5 20:24
 * @description:
 */
public class MapStructTest {
    @Test
    public void test(){

        LibInfoService instance = Guice.createInjector(new BindConfig()).getInstance(LibInfoService.class);
        LibWebInfo libWebInfo = instance.getLibWebInfo("SOE-121");
    }

}
