package pipeline;

import dto.LibWebInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @Description:
 * @Author Youjun
 * @Create 2019/8/13
 */
@Slf4j
@Singleton
public class LibInfoMarkdownPipeline {

    public String process(List<LibWebInfo> libWebInfos, Predicate<LibWebInfo> filter){
        libWebInfos.removeIf(filter);
        libWebInfos.sort(Collections.reverseOrder(Comparator.comparing(libWebInfo -> Double.valueOf(libWebInfo.getRated()))));

        StringBuilder stringBuilder = new StringBuilder();
        for (LibWebInfo libWebInfo : libWebInfos) {
            stringBuilder.append(this.format(libWebInfo));
        }
        log.info("\n" + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private String format(LibWebInfo libWebInfo){
        String noTemplate = "### %s %s\n";
        String imageTemplate = "![](%s)\n";

        return new StringBuilder()
                .append(String.format(noTemplate,libWebInfo.getNo(),libWebInfo.getRated()))
                .append(libWebInfo.getUrl())
                .append("\n")
                .append(String.format(imageTemplate,libWebInfo.getImageUrl()))
                .toString();
    }


}
