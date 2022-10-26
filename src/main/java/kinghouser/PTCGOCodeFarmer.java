package kinghouser;

import kinghouser.util.youtube.YouTubeCrawler;

import java.util.ArrayList;
import java.util.List;

public class PTCGOCodeFarmer {

    public static void main(String[] args) {
        List<String> queries = new ArrayList<>();
        YouTubeCrawler youTubeCrawler = new YouTubeCrawler(queries);
        youTubeCrawler.start();

        //Utils.init();
    }
}
