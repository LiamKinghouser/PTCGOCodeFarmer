package kinghouser.util.youtube;

import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import kinghouser.PTCGOCodeFarmer;
import kinghouser.util.OCRUtils;
import kinghouser.util.Utils;

import java.io.File;

public class YouTubeVideoScanner extends Thread {

    private final SearchResultVideoDetails searchResultVideoDetails;

    public YouTubeVideoScanner(SearchResultVideoDetails searchResultVideoDetails) {
        this.searchResultVideoDetails = searchResultVideoDetails;
    }

    public void run() {
        // OCRUtils.checkVideo(YouTubeVideoDownloader.downloadYouTubeVideo(Utils.urlFromVideoID(searchResultVideoDetails.videoId())));
        PTCGOCodeFarmer.guiThread.setDownloadingVideo(PTCGOCodeFarmer.youTubeCrawler.getIndex(this.threadId()));
        File video = YouTubeVideoDownloader.download(Utils.urlFromVideoID(searchResultVideoDetails.videoId()));
        if (video != null) {
            System.out.println("Checking video...");
            boolean successful = OCRUtils.checkVideo(video, this.threadId());
            if (!successful) System.out.println("Failed to scan video. ");
            else System.out.println("Successfully scanned video.");
        }
        endThread();
    }

    private void endThread() {
        PTCGOCodeFarmer.youTubeCrawler.removeFromRunningYouTubeVideoScanners(this.threadId());
    }
}