package kinghouser.util;

import com.github.kiulian.downloader.YoutubeDownloader;
import kinghouser.util.youtube.YouTubeVideoDownloader;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    public static File tempDirectory = new File(System.getProperty("java.io.tmpdir"));

    public static void init() {
        try {
            OCRUtils.checkVideo(YouTubeVideoDownloader.downloadYouTubeVideo("https://www.youtube.com/watch?v=CU2FU_wSX5g"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

       // PTCGOUtils.applyPTCGOCode("");
    }

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
}