package util;

/**
 * Created by PC on 6/16/2017.
 */
public class Const {
    // 网址
    public static final String LIB_URL = PropertyUtil.getProperty("LIB_URL");
    // 根据No检索
    public static final String SEARCH_BY_NO = LIB_URL + PropertyUtil.getProperty("SEARCH_BY_NO");
    // 网站英文名称
    public static final String LIB_NAME = PropertyUtil.getProperty("LIB_NAME");

    public static final String INDEX_URL = PropertyUtil.getProperty("INDEX_URL");
    public static final String ACTOR = PropertyUtil.getProperty("ACTOR");

    private Const() {
    }
}
