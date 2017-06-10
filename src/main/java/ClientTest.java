import dto.LibWebInfo;
import service.LibWebService;
import util.CommonUtil;
import util.PropertyUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Set;

public class ClientTest {
    public static String LIB_URL_SET_PATH = PropertyUtil.getProperty("LIB_URL_SET_PATH");

    public static String LIB_WEB_INFO_SET_PATH = PropertyUtil.getProperty("LIB_WEB_INFO_SET_PATH");

    public static void main(String[] args) throws IllegalAccessException, SQLException, InvocationTargetException {
        LibWebService service = new LibWebService();
        Set<LibWebInfo> localLibWebInfoList = (Set<LibWebInfo>) CommonUtil.getObject(LIB_WEB_INFO_SET_PATH);
        service.blMain(localLibWebInfoList);
    }

}
