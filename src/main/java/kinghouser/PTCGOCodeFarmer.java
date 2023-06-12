package kinghouser;

import kinghouser.util.Utils;
import kinghouser.util.ptcgo.PTCGOCodeRedeemer;
import kinghouser.util.youtube.YouTubeCrawler;

import java.util.ArrayList;
import java.util.List;

public class PTCGOCodeFarmer {

    public static Utils.GUIThread guiThread;
    public static PTCGOCodeRedeemer ptcgoCodeRedeemer;
    public static YouTubeCrawler youTubeCrawler;

    public static void main(String[] args) {
        Utils.loadNativeLibrary();

        List<String> queries = new ArrayList<>();
        queries.add("pokemon opening");
        queries.add("pokémon opening");

        queries.add("pokemon box opening");
        queries.add("pokémon box opening");

        queries.add("pokemon cards");
        queries.add("pokémon cards");

        queries.add("pokemon cards opening");
        queries.add("pokémon cards opening");

        queries.add("pokemon open");
        queries.add("pokémon open");

        queries.add("pokemon cards open");
        queries.add("pokémon cards open");

        queries.add("pokemon box open");
        queries.add("pokémon box open");

        guiThread = new Utils.GUIThread();
        guiThread.start();

        ptcgoCodeRedeemer = new PTCGOCodeRedeemer();
        ptcgoCodeRedeemer.start();

        youTubeCrawler = new YouTubeCrawler(queries);
        youTubeCrawler.start();
    }
}
