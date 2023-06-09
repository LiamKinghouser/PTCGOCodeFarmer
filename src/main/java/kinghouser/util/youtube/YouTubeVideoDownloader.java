package kinghouser.util.youtube;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.Extension;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import io.github.gaeqs.javayoutubedownloader.JavaYoutubeDownloader;
import io.github.gaeqs.javayoutubedownloader.decoder.MultipleDecoderMethod;
import io.github.gaeqs.javayoutubedownloader.stream.StreamOption;
import io.github.gaeqs.javayoutubedownloader.stream.YoutubeVideo;
import io.github.gaeqs.javayoutubedownloader.stream.download.StreamDownloader;
import kinghouser.util.Utils;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class YouTubeVideoDownloader {

    private static final YoutubeDownloader youtubeDownloader = new YoutubeDownloader();

    public static File downloadYouTubeVideo(String url) {
        System.out.println("Starting YouTube download for: " + url);
        long startTime = System.currentTimeMillis();

        String videoId = Utils.getVideoID(url);

        System.out.println(videoId);

        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = youtubeDownloader.getVideoInfo(request);
        VideoInfo video = response.data();

        if (video == null) {
            System.out.println("video is null");
            return null;
        }

        List<Format> videoFormats = video.findFormats(format -> format.extension() == Extension.WEBM && (format instanceof VideoFormat && ((VideoFormat) format).fps() <= 30));

        if (videoFormats.size() == 0) {
            videoFormats = video.findFormats(format -> format instanceof VideoFormat && ((VideoFormat) format).fps() <= 30);
        }

        if (videoFormats.size() == 0) {
            videoFormats = video.findFormats(format -> format instanceof VideoFormat);
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
        System.out.println(outputDir.getPath());
        // System.out.println("Video downloaded. Time elapsed: " + (Utils.getTime((int)(System.currentTimeMillis() - startTime) / 1000)));
        return data;
    }

    public static File download(String url) {
        try {
            YoutubeVideo video = JavaYoutubeDownloader.decode(url, MultipleDecoderMethod.OR, "html", "embedded");

            StreamOption option = video.getStreamOptions().stream()
                    .filter(target -> target.getType().hasVideo())
                    .max(Comparator.comparingInt(o -> o.getType().getVideoQuality().ordinal())).orElse(null);

            if (option == null) option = video.getStreamOptions().get(0);

            File folder = Utils.tempDirectory;

            File file = new File(folder, "youtube-video-" + System.currentTimeMillis() + "." + option.getType().getContainer().toString().toLowerCase());
            file.deleteOnExit();

            StreamDownloader downloader = new StreamDownloader(option, file, null);
            Thread downloaderThread = new Thread(downloader);

            downloaderThread.start();
            try {
                downloaderThread.join();
            } catch (InterruptedException e) {
                return null;
            }
            return file;
        } catch (Exception e) {
            return null;
        }
    }
}