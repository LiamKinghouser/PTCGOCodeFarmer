package kinghouser.util;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import kinghouser.PTCGOCodeFarmer;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class Utils {

    public static final String ENTER_CODES_URL = "https://www.pokemon.com/us/pokemon-trainer-club/enter-codes";
    public static final int LOWER_FPS_THRESHOLD_SECONDS = 300;

    private static final YoutubeDownloader youtubeDownloader = new YoutubeDownloader();

    public static void init() {
        handleLoggers();
        try {
            OCRUtils.checkVideo(new File(PTCGOCodeFarmer.class.getClassLoader().getResource("vid2.mp4").toURI()));
            //OCRUtils.checkVideo(new File(downloadYouTubeVideo("https://www.youtube.com/watch?v=mDgn1UDw6Io").toURI()));
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

    /*
    private static File downloadYouTubeVideo(String url) {
        String videoId = url.split("www.youtube.com/watch?v=")[1];


        File outputDir = new File(System.getProperty("java. io. tmpdir"));

        RequestVideoFileDownload requestVideoFileDownload = new RequestVideoFileDownload(videoFormats.get(0))
                .saveTo(outputDir)
                .renameTo("youtube-video-" + System.currentTimeMillis(), ".mp4")
                .overwriteIfExists(true);
        response = youtubeDownloader.downloadVideoFile(requestVideoFileDownload);
    }

     */
}