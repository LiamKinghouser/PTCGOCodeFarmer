package kinghouser.util;

import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

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

    public static class GUIThread extends Thread {

        private static JLabel[] threadSnapshots = new JLabel[5];

        public void run() {
            JFrame frame = new JFrame();
            frame.setSize(800, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("PTCGO Code Farmer");

            // Create main panel with a grid layout
            JPanel mainPanel = new JPanel(new GridLayout(1, 5));
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            frame.add(mainPanel);

            // Load sample images
            BufferedImage[] images = loadSampleImages();

            // Create and add image labels to the main panel
            for (BufferedImage image : images) {
                JLabel threadSnapshot = new JLabel(new ImageIcon(image));
                threadSnapshot.setHorizontalAlignment(SwingConstants.CENTER);
                mainPanel.add(threadSnapshot);
                int index = getFirstEmptyIndex();
                if (index != -1) threadSnapshots[index] = threadSnapshot;
            }

            // Adjust frame size based on content
            frame.pack();
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);
        }

        private BufferedImage[] loadSampleImages() {
            BufferedImage[] images = new BufferedImage[5];

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

        private void updateImage(int index, BufferedImage image) {
            threadSnapshots[index].setIcon(new ImageIcon(image));
        }

        private int getFirstEmptyIndex() {
            for (int i = 0; i < 5; i++) {
                if (threadSnapshots[i] == null) return i;
            }
            return -1;
        }
    }
}