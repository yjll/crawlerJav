package util;

/**
 * Created by PC on 6/16/2017.
 */
public class Const {
    // 网址
    public static final String LIB_URL = PropertyUtil.getProperty("LIB_URL");
    // 根据评分排名URL
    public static final String BEST_RATED = LIB_URL + PropertyUtil.getProperty("BEST_RATED");
    // 根据No检索
    public static final String SEARCH_BY_NO = LIB_URL + PropertyUtil.getProperty("SEARCH_BY_NO");
    // 网站英文名称
    public static final String LIB_NAME = PropertyUtil.getProperty("LIB_NAME");
    // 本地图片root目录
    public static final String IMAGE_ROOT_PATH = PropertyUtil.getProperty("IMAGE_ROOT_PATH");
    // Lib链接本地存储路径
    public static final String LIB_URL_SET_PATH = PropertyUtil.getProperty("LIB_URL_SET_PATH");
    // Lib网页信息本地存储路径
    public static final String LIB_WEB_INFO_SET_PATH = PropertyUtil.getProperty("LIB_WEB_INFO_SET_PATH");
}
