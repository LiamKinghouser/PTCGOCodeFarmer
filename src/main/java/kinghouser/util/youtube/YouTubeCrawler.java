package kinghouser.util.youtube;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestSearchContinuation;
import com.github.kiulian.downloader.downloader.request.RequestSearchResult;
import com.github.kiulian.downloader.model.search.SearchResult;
import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import com.github.kiulian.downloader.model.search.field.SortField;
import com.github.kiulian.downloader.model.search.field.TypeField;
import com.github.kiulian.downloader.model.search.field.UploadDateField;

import java.util.List;

public class YouTubeCrawler {

    private final List<String> queries;

    private final YoutubeDownloader youtubeDownloader;

    public YouTubeCrawler(List<String> queries) {
        this.queries = queries;
        youtubeDownloader = new YoutubeDownloader();
    }

    public void start() {
        RequestSearchResult request = new RequestSearchResult("pokémon opening")
                .type(TypeField.VIDEO)
                .uploadedThis(UploadDateField.MONTH)
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

    private static void printResults(SearchResult result) {
        List<SearchResultVideoDetails> videos = result.videos();

        for (SearchResultVideoDetails searchResultVideoDetails : videos) {
            System.out.println(searchResultVideoDetails.badges() + " | " + searchResultVideoDetails.title() + " | " + searchResultVideoDetails.viewCount() + " | " + urlFromVideoID(searchResultVideoDetails.videoId()));
        }
    }

    private static String urlFromVideoID(String id) {
        return "https://youtube.com/watch?v=" + id;
    }
}