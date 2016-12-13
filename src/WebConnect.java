import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebConnect {
    public void getWeb() throws IOException {
        String webLinks = "";
        String url = "http://www.javl10.com/cn/vl_bestrated.php?list&mode=2&page=";
        for(int i = 1;i<=25;i++){
            Document doc = Jsoup.connect(url + i).userAgent("Mozilla").get();
            Elements links = doc.select("a[href]");
            Pattern linkPattern = Pattern.compile(".*v=.*");
            Matcher linkMatcher;
            for (Element link : links) {
                linkMatcher = linkPattern.matcher(link.attr("href"));
                if (linkMatcher.matches()) {
                    webLinks += ("http://www.javl10.com/cn" + link.attr("href").substring(1) + "\n");
                }
            }
        }
        System.out.print(webLinks);

    }

    public static void main(String[] args) throws IOException {
        WebConnect webConnect = new WebConnect();
        webConnect.getWeb();
    }

}
