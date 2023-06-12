package kinghouser.util.ptcgo;

import kinghouser.PTCGOCodeFarmer;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class PTCGOCodeRedeemer extends Thread {

    public AtomicBoolean running = new AtomicBoolean(true);

    public ArrayList<String> ptcgoCodeQueue = new ArrayList<>();

    public ArrayList<String> submittedCodes = new ArrayList<>();

    private Robot robot;

    public void run() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.out.println("PTCGO Code Redeemer Robot failed to initialize.");
            throw new RuntimeException(e);
        }

        while (running.get()) {
            if (ptcgoCodeQueue.size() != 0) {
                ArrayList<String> codes = ptcgoCodeQueue;
                for (String code : codes) {
                    if (!submittedCodes.contains(code)) {
                        redeemCode(code);
                        submittedCodes.add(code);
                    }
                }
                ptcgoCodeQueue.clear();
            }
        }
    }

    public synchronized void addCodeToQueue(String code) {
        ptcgoCodeQueue.add(code);
    }

    private void redeemCode(String code) {
        try {
            focusPTCGLive();

            robot.delay(1000);

            clickUIElement("images/enter_code_box.png");

            robot.delay(100);

            typeCode(code);

            robot.delay(200);

            clickUIElement("images/submit_code_button.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void focusPTCGLive() {
        String applescriptCommand = """
                tell application "System Events"
                set frontmost of process "Pokemon TCG Live" to true
                end tell""";

        String[] args = { "osascript", "-e", applescriptCommand };
        try {
            Runtime.getRuntime().exec(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void clickUIElement(String path) {
        try {
            Point point = null;
            while (point == null) {
                point = findUIElement(path);
            }

            robot.mouseMove(point.x, point.y);

            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(100);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Point findUIElement(String path) {
        try {
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

            Mat screenshotMat = bufferedImageToMat(screenshot);

            InputStream inputStream = PTCGOCodeFarmer.class.getClassLoader().getResourceAsStream(path);
            if (inputStream == null) return null;

            BufferedImage image = ImageIO.read(inputStream);

            Mat targetImage = bufferedImageToMat(image);

            Mat result = new Mat();

            // Resize the target mat to account for scaling differences between robot.createScreenCapture() and manual screenshot
            targetImage = resizeMat(targetImage);

            int result_cols = screenshotMat.cols() - targetImage.cols() + 1;
            int result_rows = screenshotMat.rows() - targetImage.rows() + 1;
            result.create( result_rows, result_cols, CvType.CV_32FC1 );

            screenshotMat.create(new Size(screenshotMat.width(), screenshotMat.height()), CvType.CV_8UC3);
            targetImage.create(new Size(targetImage.width(), targetImage.height()), CvType.CV_8UC3);

            Imgproc.matchTemplate(screenshotMat, targetImage, result, Imgproc.TM_SQDIFF);
            Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

            // / Localizing the best match with minMaxLoc
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

            org.opencv.core.Point matchLoc = mmr.minLoc;

            Rect rect = new Rect((int) matchLoc.x, (int) matchLoc.y, targetImage.width(), targetImage.height());

            return new Point(rect.x + (rect.width / 2), rect.y + (rect.height / 2));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Mat resizeMat(Mat image) {
        Mat resizedImage = new Mat();
        Size newSize = new Size((double) image.width() / 2, (double) image.height() / 2);
        Imgproc.resize(image, resizedImage, newSize);
        return resizedImage;
    }

    private Mat bufferedImageToMat(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        Mat mat = new Mat(height, width, CvType.CV_8UC3);

        int[] data = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, data, 0, width);

        byte[] bytes = new byte[width * height * (int) mat.elemSize()];
        int[] pixel;
        int index = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                pixel = getPixelData(data[row * width + col]);
                bytes[index++] = (byte) pixel[2]; // R
                bytes[index++] = (byte) pixel[1]; // G
                bytes[index++] = (byte) pixel[0]; // B
            }
        }

        mat.put(0, 0, bytes);

        return mat;
    }

    private int[] getPixelData(int pixel) {
        int[] rgb = new int[3];
        rgb[0] = (pixel >> 16) & 0xFF; // R
        rgb[1] = (pixel >> 8) & 0xFF; // G
        rgb[2] = pixel & 0xFF; // B
        return rgb;
    }

    private void typeCode(String code) {
        String applescriptCommand =  "tell application \"System Events\"\n" +
                "keystroke \"" + code + "\"\n" +
                "end tell";

        String[] keystrokeArgs = { "osascript", "-e", applescriptCommand };
        try {
            Runtime.getRuntime().exec(keystrokeArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}