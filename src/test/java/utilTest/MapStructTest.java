package utilTest;

import com.google.inject.Guice;
import config.BindConfig;
import dto.LibWebInfo;
import org.junit.Test;
import pipeline.LibInfoPipeline;

/**
 * @author: zijing
 * @date: 2018/6/5 20:24
 * @description:
 */
public class MapStructTest {
    @Test
    public void test(){

        LibInfoPipeline instance = Guice.createInjector(new BindConfig()).getInstance(LibInfoPipeline.class);
        LibWebInfo libWebInfo = instance.getLibWebInfo("SOE-121");
    }

}
