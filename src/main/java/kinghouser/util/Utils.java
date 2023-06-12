package kinghouser.util;

import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import kinghouser.PTCGOCodeFarmer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    public static File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
    public static int MAX_VIDEOS = 10;
    public static int MAX_THREADS = 5;
    public static int MAX_VIDEO_LENGTH_SECONDS = 300;
    public static int MAX_VIDEO_VIEW_COUNT = 5;

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

    public static String getTime(int seconds) {
        int sec = seconds % 60;
        int min = (seconds / 60) % 60;
        int hours = (seconds / 60) / 60;

        return (String.valueOf(hours).length() > 1 ? hours : ("0" + hours)) + ":" + (String.valueOf(min).length() > 1 ? min : ("0" + min) + ":" + (String.valueOf(sec).length() > 1 ? sec : ("0" + sec)));
    }

    public static String urlFromVideoID(String id) {
        return "https://youtube.com/watch?v=" + id;
    }

    public static boolean searchResultVideoDetailsFitsCriteria(SearchResultVideoDetails searchResultVideoDetails) {
        return !searchResultVideoDetails.isLive() && searchResultVideoDetails.lengthSeconds() <= Utils.MAX_VIDEO_LENGTH_SECONDS && searchResultVideoDetails.viewCount() <= Utils.MAX_VIDEO_VIEW_COUNT && searchResultVideoDetails.viewCount() != -1;
    }

    public static void loadNativeLibrary() {
        Path jarPath;
        try {
            jarPath = Paths.get(PTCGOCodeFarmer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String path = jarPath.getParent().toString() + File.separator + "lib" + File.separator + "libopencv_java460.dylib";
        System.load(path);
    }

    public static class GUIThread extends Thread {

        private static final JLabel[] threadSnapshots = new JLabel[Utils.MAX_THREADS];

        public void run() {
            JFrame frame = new JFrame();
            frame.setSize(1200, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("PTCGO Code Farmer");

            // Create main panel with a grid layout
            JPanel mainPanel = new JPanel(new GridLayout(1, Utils.MAX_THREADS));
            frame.add(mainPanel, BorderLayout.NORTH);

            // Load sample images
            BufferedImage[] images = loadPlaceholders();

            // Create and add image labels to the main panel
            for (BufferedImage image : images) {
                JLabel threadSnapshot = new JLabel(new ImageIcon(image));
                threadSnapshot.setBorder(new EmptyBorder(0, 10, 0, 10));
                threadSnapshot.setHorizontalAlignment(SwingConstants.CENTER);
                mainPanel.add(threadSnapshot);
                int index = getFirstEmptyIndex();
                if (index != -1) threadSnapshots[index] = threadSnapshot;
            }

            frame.setVisible(true);
        }

        private BufferedImage[] loadPlaceholders() {
            BufferedImage[] images = new BufferedImage[Utils.MAX_THREADS];

            int width = 200;
            int height = 200;
            for (int i = 0; i < images.length; i++) {
                images[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = images[i].createGraphics();
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, width, height);
                g2d.dispose();
            }
            return images;
        }

        public void updateImage(int index, BufferedImage image) {
            threadSnapshots[index].setIcon(new ImageIcon(image));
        }

        public void resetImage(int index) {
            BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, 200, 200);
            g2d.dispose();
            threadSnapshots[index].setIcon(new ImageIcon(bufferedImage));
        }

        public void setDownloadingVideo(int index) {
            BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, 200, 200);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Downloading...", 50, 100);
            g2d.dispose();
            threadSnapshots[index].setIcon(new ImageIcon(bufferedImage));
        }

        private int getFirstEmptyIndex() {
            for (int i = 0; i < Utils.MAX_THREADS; i++) {
                if (threadSnapshots[i] == null) return i;
            }
            return -1;
        }
    }
}