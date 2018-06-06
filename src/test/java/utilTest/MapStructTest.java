package utilTest;
import com.google.common.collect.Lists;

import com.google.gson.Gson;
import dto.LibWebInfo;
import dto.VideoInfo;
import org.junit.Test;

/**
 * @author: zijing
 * @date: 2018/6/5 20:24
 * @description:
 */
public class MapStructTest {
    @Test
    public void test(){

        LibWebInfo libWebInfo = new LibWebInfo();
        libWebInfo.setUrl("");
        libWebInfo.setNo("");
        libWebInfo.setTile("");
        libWebInfo.setDate("");
        libWebInfo.setDuration("1");
        libWebInfo.setRated("1");
        libWebInfo.setCategoryList(Lists.newArrayList());
        libWebInfo.setImageUrl("");
        libWebInfo.setActorList(Lists.newArrayList());


        VideoInfo map = MapperInterface.INTERFACE.map(libWebInfo);
        System.out.println(new Gson().toJson(map));
    }

}
