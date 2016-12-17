package dto;

import java.lang.reflect.Field;
import java.util.List;

public class LibWebInfo {
    private String number;

    private String tile;

    private String date;

    private String duration;

    private String rated;

    private List<String> categoryList;

    private String actor;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    @Override
    public String toString() {
        String str = "";

        Class clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);

                Object obj = field.get(this);
                str += field.getName() + "\t" + obj + "\n";

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return str;

    }
}
