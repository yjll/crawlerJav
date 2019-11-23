package dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
@Data
public class LibWebInfo implements Serializable {

    private String url;
    private String no;

    private String title;

    private String date;

    private String duration;
    /**
     * 评分
     */
    private String rated;

    private List<String> categoryList;

    private List<String> actorList;

    private String imageUrl;

    public String getRated(){
        return StringUtils.isNotBlank(rated) && !rated.startsWith("1")?rated:"1";
    }




}
