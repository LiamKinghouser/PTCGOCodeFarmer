package kinghouser.util.youtube;

import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import kinghouser.util.OCRUtils;
import kinghouser.util.Utils;

public class YouTubeVideoScanner extends Thread {

    private SearchResultVideoDetails searchResultVideoDetails;

    public YouTubeVideoScanner(SearchResultVideoDetails searchResultVideoDetails) {
        this.searchResultVideoDetails = searchResultVideoDetails;
        this.start();
    }

    public void run() {
        scan(searchResultVideoDetails);
    }

    public void scan(SearchResultVideoDetails searchResultVideoDetails) {
        OCRUtils.checkVideo(YouTubeVideoDownloader.downloadYouTubeVideo(Utils.urlFromVideoID(searchResultVideoDetails.videoId())));
    }
}