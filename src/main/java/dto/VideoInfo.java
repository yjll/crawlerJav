package dto;

import java.util.Date;

public class VideoInfo {
    private String no;

    private String javlibUrl;

    private String title;

    private Date date;

    private Float rated;

    private Integer duretion;


    private Date systemTime;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no == null ? null : no.trim();
    }

    public String getJavlibUrl() {
        return javlibUrl;
    }

    public void setJavlibUrl(String javlibUrl) {
        this.javlibUrl = javlibUrl == null ? null : javlibUrl.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getDuretion() {
        return duretion;
    }

    public void setDuretion(Integer duretion) {
        this.duretion = duretion;
    }

    public Date getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(Date systemTime) {
        this.systemTime = systemTime;
    }

    public Float getRated() {
        return rated;
    }

    public void setRated(Float rated) {
        this.rated = rated;
    }
}