package kinghouser.util.youtube;

import io.github.gaeqs.javayoutubedownloader.JavaYoutubeDownloader;
import io.github.gaeqs.javayoutubedownloader.decoder.MultipleDecoderMethod;
import io.github.gaeqs.javayoutubedownloader.exception.DownloadException;
import io.github.gaeqs.javayoutubedownloader.stream.StreamOption;
import io.github.gaeqs.javayoutubedownloader.stream.YoutubeVideo;
import io.github.gaeqs.javayoutubedownloader.stream.download.StreamDownloader;
import kinghouser.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;

public class YouTubeVideoDownloader {

    public static File download(String url) {
        try {
            YoutubeVideo video = JavaYoutubeDownloader.decodeOrNull(url, MultipleDecoderMethod.OR, "html", "embedded");
            if (video == null) return null;

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