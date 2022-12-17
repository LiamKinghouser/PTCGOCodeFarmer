package kinghouser.util;

import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;

import java.io.File;

public class Utils {

    public static File tempDirectory = new File(System.getProperty("java.io.tmpdir"));

    public static String findAverageSpeed(double frames, double time) {
        return String.format("%.2f", (frames / (time / 1000)));
    }

    public static boolean isPTCGOCode(String s) {
        if (s.contains("http")) return false;
        return s.split("-").length == 4;
    }

    public static String getVideoID(String url) {
        StringBuilder id = new StringBuilder();
        for (int i = url.length() - 1; i > 0; i--) {
            if (Character.isLetterOrDigit(url.charAt(i)) || url.charAt(i) == '_' || url.charAt(i) == '-') id.append(url.charAt(i));
            else break;
        }
        return id.reverse().toString();
    }

    public static String getTime(int seconds) {
        int sec = seconds % 60;
        int min = (seconds / 60) % 60;
        int hours = (seconds / 60) / 60;

        return (String.valueOf(hours).length() > 1 ? hours : ("0" + hours)) + ":" + (String.valueOf(min).length() > 1 ? min : ("0" + min) + ":" + (String.valueOf(sec).length() > 1 ? sec : ("0" + sec)));
    }

    public static String urlFromVideoID(String id) {
        return "https://youtube.com/watch?v=" + id;
    }

    public static boolean searchResultVideoDetailsFitsCriteria(SearchResultVideoDetails searchResultVideoDetails) {
        return !searchResultVideoDetails.isLive() && searchResultVideoDetails.lengthSeconds() < 300 && searchResultVideoDetails.viewCount() <= 5 && searchResultVideoDetails.viewCount() != -1;
    }
}