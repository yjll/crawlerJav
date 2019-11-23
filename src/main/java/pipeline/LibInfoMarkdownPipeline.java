package pipeline;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import dto.LibWebInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import util.Const;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author Youjun
 * @Create 2019/8/13
 */
@Slf4j
@Singleton
public class LibInfoMarkdownPipeline {

    private static final Predicate<LibWebInfo> predicate = libWebInfo ->
            libWebInfo.getTitle().contains(Const.ACTOR) &&
                    Double.parseDouble(libWebInfo.getRated()) >= 8;

    public String process(List<LibWebInfo> libWebInfos) {

        return libWebInfos.stream()
                .filter(predicate)
                .sorted(Collections.reverseOrder(Comparator.comparing(libWebInfo -> Double.valueOf(libWebInfo.getRated()))))
                .map(this::format)
                .collect(Collectors.joining());
    }

    private String format(LibWebInfo libWebInfo) {
        String noTemplate = "### %s %s\n";
        String imageTemplate = "![](%s)\n";

        return new StringBuilder()
                .append(String.format(noTemplate, libWebInfo.getNo(), libWebInfo.getRated()))
                .append(libWebInfo.getUrl())
                .append("\n")
                .append(String.format(imageTemplate, libWebInfo.getImageUrl()))
                .toString();
    }


}
