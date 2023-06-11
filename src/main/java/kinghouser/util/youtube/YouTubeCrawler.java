package kinghouser.util.youtube;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestSearchContinuation;
import com.github.kiulian.downloader.downloader.request.RequestSearchResult;
import com.github.kiulian.downloader.model.search.SearchResult;
import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import com.github.kiulian.downloader.model.search.field.SortField;
import com.github.kiulian.downloader.model.search.field.TypeField;
import com.github.kiulian.downloader.model.search.field.UploadDateField;
import kinghouser.PTCGOCodeFarmer;
import kinghouser.util.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class YouTubeCrawler {

    private final List<String> queries;
    private final YoutubeDownloader youtubeDownloader;
    public final ArrayList<String> checkedVideoIDs;
    private final ArrayList<SearchResult> searchResults;
    public Long[] runningYouTubeVideoScanners = new Long[Utils.MAX_THREADS];

    public YouTubeCrawler(List<String> queries) {
        this.queries = queries;
        youtubeDownloader = new YoutubeDownloader();
        checkedVideoIDs = new ArrayList<>();
        searchResults = new ArrayList<>();
    }

    public void start() {
        for (String query : queries) {
            search(query);
        }

        ArrayList<String> videoIDs = new ArrayList<>();

        for (SearchResult searchResult : searchResults) {
            for (SearchResultVideoDetails searchResultVideoDetails : searchResult.videos()) {
                if (!videoIDs.contains(searchResultVideoDetails.videoId()) && Utils.searchResultVideoDetailsFitsCriteria(searchResultVideoDetails)) {
                    videoIDs.add(searchResultVideoDetails.videoId());
                }
            }
        }
        ArrayList<SearchResultVideoDetails> videos = sortByViewCount();

        System.out.println("Found " + videos.size() + " videos. Scanning now.");

        for (SearchResultVideoDetails searchResultVideoDetails : videos) {
            System.out.println(searchResultVideoDetails.badges() + " | " + searchResultVideoDetails.title() + " | " + searchResultVideoDetails.viewCount() + " | " + searchResultVideoDetails.lengthSeconds() + " | " + Utils.urlFromVideoID(searchResultVideoDetails.videoId()));
        }

        for (SearchResultVideoDetails searchResultVideoDetails : videos) {
            PTCGOCodeFarmer.youTubeCrawler.checkedVideoIDs.add(searchResultVideoDetails.videoId());
            while (this.getRunningYouTubeVideoScannersCount() >= Utils.MAX_THREADS) {}

            YouTubeVideoScanner youTubeVideoScanner = new YouTubeVideoScanner(searchResultVideoDetails);
            youTubeVideoScanner.start();
            this.addToRunningYouTubeVideoScanners(youTubeVideoScanner.threadId());
        }
    }

    public void search(String query) {
        RequestSearchResult request = new RequestSearchResult(query)
                .type(TypeField.VIDEO)
                .uploadedThis(UploadDateField.DAY)
                .forceExactQuery(true)
                .sortBy(SortField.VIEW_COUNT);

        SearchResult searchResult = youtubeDownloader.search(request).data();

        searchResults.add(searchResult);

        while (searchResult != null && searchResult.hasContinuation()) {
            RequestSearchContinuation nextRequest = new RequestSearchContinuation(searchResult);
            searchResult = youtubeDownloader.searchContinuation(nextRequest).data();
            if (searchResult != null) searchResults.add(searchResult);
        }
    }

    private ArrayList<SearchResultVideoDetails> sortByViewCount() {
        ArrayList<SearchResultVideoDetails> videos = new ArrayList<>();
        ArrayList<String> videoIDs = new ArrayList<>();

        class SortByViewCount implements Comparator<SearchResultVideoDetails> {
            public int compare(SearchResultVideoDetails a, SearchResultVideoDetails b)
            {
                return (int) (a.viewCount() - b.viewCount());
            }
        }

        for (SearchResult searchResult : searchResults) {
            for (SearchResultVideoDetails searchResultVideoDetails : searchResult.videos()) {
                if (!videoIDs.contains(searchResultVideoDetails.videoId()) && Utils.searchResultVideoDetailsFitsCriteria(searchResultVideoDetails)) {
                    videoIDs.add(searchResultVideoDetails.videoId());
                    videos.add(searchResultVideoDetails);
                }
            }
        }
        videos.sort(new SortByViewCount());
        return videos;
    }

    public synchronized void removeFromRunningYouTubeVideoScanners(long threadID) {
        for (int i = 0; i < Utils.MAX_THREADS; i++) {
            if (runningYouTubeVideoScanners[i].equals(threadID)) runningYouTubeVideoScanners[i] = null;
        }
    }

    public synchronized void addToRunningYouTubeVideoScanners(long threadID) {
        for (int i = 0; i < Utils.MAX_THREADS; i++) {
            if (runningYouTubeVideoScanners[i] == null) {
                runningYouTubeVideoScanners[i] = threadID;
                break;
            }
        }
    }

    public synchronized int getRunningYouTubeVideoScannersCount() {
        int count = 0;
        for (int i = 0; i < Utils.MAX_THREADS; i++) {
            if (runningYouTubeVideoScanners[i] != null) count++;
        }
        return count;
    }

    public synchronized int getIndex(long threadID) {
        for (int i = 0; i < Utils.MAX_THREADS; i++) {
            if (runningYouTubeVideoScanners[i].equals(threadID)) return i;
        }
        return -1;
    }
}