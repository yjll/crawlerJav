package test.util;

/**
 * Created by PC on 5/5/2017.
 */
public class Converter {
    public static void main(String[] args) {
        Object obj = "1";
        Integer i = obj==null?null:(int)obj;
        System.out.println(i);

    }
}
