import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class WebsiteRaces {
    private Document doc;

    public WebsiteRaces(String url, int season) {
        try {
            this.doc = Jsoup.connect(url + "/" + season + "/races.html").get();
        } catch (IOException e) {
            System.out.println("You cannot get the website");
        }
    }

    public Document getDoc() {
        return doc;
    }
}
