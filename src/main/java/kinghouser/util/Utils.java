package kinghouser.util;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Utils {

    public static final int LOWER_FPS_THRESHOLD_SECONDS = 300;

    public static File tempDirectory = new File(System.getProperty("java.io.tmpdir"));

    public static void init() {
        handleLoggers();
        try {
            //OCRUtils.checkVideo(new File(PTCGOCodeFarmer.class.getClassLoader().getResource("vid2.mp4").toURI()));
            OCRUtils.checkVideo(YouTubeVideoDownloader.downloadYouTubeVideo("https://www.youtube.com/watch?v=C0YnCEd8q8s"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

       // PTCGOUtils.applyPTCGOCode("");
    }

    private static void handleLoggers() {
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
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

    public static String getLength(int seconds) {
        int sec = seconds % 60;
        int min = (seconds / 60) % 60;
        int hours = (seconds / 60) / 60;

        return hours + ":" + min + ":" + sec;
    }
}