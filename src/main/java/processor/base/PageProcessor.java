package processor.base;

import java.io.IOException;

/**
 * @author Youjun
 * @create 2019/8/13
 * @since 1.0.0
 */
public interface PageProcessor<T> {

    T process(String url) throws IOException;



}
