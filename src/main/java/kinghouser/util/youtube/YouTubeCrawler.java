package kinghouser.util.youtube;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestSearchContinuation;
import com.github.kiulian.downloader.downloader.request.RequestSearchResult;
import com.github.kiulian.downloader.model.search.SearchResult;
import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import com.github.kiulian.downloader.model.search.field.SortField;
import com.github.kiulian.downloader.model.search.field.TypeField;
import com.github.kiulian.downloader.model.search.field.UploadDateField;
import kinghouser.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class YouTubeCrawler {

    private final List<String> queries;

    private final YoutubeDownloader youtubeDownloader;

    private ArrayList<String> checkedVideoIDs;

    public YouTubeCrawler(List<String> queries) {
        this.queries = queries;
        youtubeDownloader = new YoutubeDownloader();
        checkedVideoIDs = new ArrayList<>();
    }

    public void start() {
        for (String query : queries) {
            search(query);
        }
    }

    public void search(String query) {
        RequestSearchResult request = new RequestSearchResult(query)
                .type(TypeField.VIDEO)
                .uploadedThis(UploadDateField.HOUR)
                .forceExactQuery(true)
                .sortBy(SortField.VIEW_COUNT);

        int resultsCount = 0;

        SearchResult result = youtubeDownloader.search(request).data();
        resultsCount = resultsCount + result.videos().size();

        printResults(result);

        while (result.hasContinuation()) {
            RequestSearchContinuation nextRequest = new RequestSearchContinuation(result);
            result = youtubeDownloader.searchContinuation(nextRequest).data();

            resultsCount = resultsCount + result.videos().size();
            printResults(result);
        }

        System.out.println("Found " + resultsCount + " videos");
    }

    private void printResults(SearchResult result) {
        List<SearchResultVideoDetails> videos = result.videos();

        for (SearchResultVideoDetails searchResultVideoDetails : videos) {
            if (searchResultVideoDetails.viewCount() < 5 && !this.checkedVideoIDs.contains(searchResultVideoDetails.videoId())) {
                System.out.println(searchResultVideoDetails.badges() + " | " + searchResultVideoDetails.title() + " | " + searchResultVideoDetails.viewCount() + " | " + Utils.urlFromVideoID(searchResultVideoDetails.videoId()));
            }
            checkedVideoIDs.add(searchResultVideoDetails.videoId());
        }
    }
}