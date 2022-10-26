package kinghouser.util.youtube;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.Extension;
import com.github.kiulian.downloader.model.Filter;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import kinghouser.util.Utils;

import java.io.File;
import java.util.List;

public class YouTubeVideoDownloader {

    private static final YoutubeDownloader youtubeDownloader = new YoutubeDownloader();

    public static File downloadYouTubeVideo(String url) {
        System.out.println("Starting YouTube download for: " + url);
        long startTime = System.currentTimeMillis();

        String videoId = Utils.getVideoID(url);

        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = youtubeDownloader.getVideoInfo(request);
        VideoInfo video = response.data();

        List<Format> videoFormats = video.findFormats(new Filter<>() {
            @Override
            public boolean test(Format format) {
                return format.extension() == Extension.WEBM && (format instanceof VideoFormat && ((VideoFormat) format).fps() <= 30);
            }
        });

        if (videoFormats.size() == 0) {
            videoFormats = video.findFormats(format -> format instanceof VideoFormat && ((VideoFormat) format).fps() <= 30);
        }

        File outputDir = Utils.tempDirectory;
        Format format = videoFormats.get(0);

        RequestVideoFileDownload requestVideoFileDownload = new RequestVideoFileDownload(format)
                .saveTo(outputDir)
                .renameTo("youtube-video-" + System.currentTimeMillis())
                .overwriteIfExists(false);
        Response<File> fileResponse = youtubeDownloader.downloadVideoFile(requestVideoFileDownload);
        File data = fileResponse.data();
        data.deleteOnExit();
        System.out.println("Video downloaded. Time elapsed: " + (Utils.getTime((int)(System.currentTimeMillis() - startTime) / 1000)));
        return data;
    }
}
