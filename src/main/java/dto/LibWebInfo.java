package dto;

import java.io.Serializable;
import java.util.List;

public class LibWebInfo implements Serializable {
    private String url;
    private String no;

    private String tile;

    private String date;

    private String duration;

    private String rated;

    private List<String> categoryList;

    private List<String> actorList;

    private String imageUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getActorList() {
        return actorList;
    }

    public void setActorList(List<String> actorList) {
        this.actorList = actorList;
    }

    @Override
    public String toString() {
        return "LibWebInfo{" +
                "url='" + url + '\'' +
                ", no='" + no + '\'' +
                ", tile='" + tile + '\'' +
                ", date='" + date + '\'' +
                ", duration='" + duration + '\'' +
                ", rated='" + rated + '\'' +
                ", categoryList=" + categoryList +
                ", actorList=" + actorList +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
