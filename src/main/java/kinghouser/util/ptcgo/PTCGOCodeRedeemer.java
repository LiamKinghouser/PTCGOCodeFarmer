package kinghouser.util.ptcgo;

import kinghouser.PTCGOCodeFarmer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

            robot.delay(200);

            clickElement("images/enter_code_box.png");

            robot.delay(200);

            typeCode(code);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void focusPTCGLive() {
        String applescriptCommand =  "tell application \"System Events\"\n" +
                "set frontmost of process \"Pokemon TCG Live\" to true\n" +
                "end tell";

        String[] args = { "osascript", "-e", applescriptCommand };
        try {
            Runtime.getRuntime().exec(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void clickElement(String path) {
        try {
            Point point = null;
            while (point == null) {
                point = findUIElement(path);
            }
            System.out.println(point.x + ", " + point.y);

            robot.mouseMove(point.x, point.y);

            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(100);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Point findUIElement(String path) {
        System.out.println("Checking for code box...");
        try {
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

            Mat screenshotMat = bufferedImageToMat(screenshot);

            InputStream inputStream = PTCGOCodeFarmer.class.getClassLoader().getResourceAsStream(path);
            if (inputStream == null) return null;

            BufferedImage image = ImageIO.read(inputStream);

            Mat targetImage = bufferedImageToMat(image);

            Mat result = new Mat();

            screenshotMat.create(new Size(screenshotMat.width(), screenshotMat.height()), CvType.CV_8UC3);
            targetImage.create(new Size(targetImage.width(), targetImage.height()), CvType.CV_8UC3);

            Imgproc.matchTemplate(screenshotMat, targetImage, result, Imgproc.TM_CCOEFF);

            double threshold = 100;
            Mat binaryResult = new Mat();
            Core.compare(result, new Scalar(threshold), binaryResult, Core.CMP_GE);

            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(binaryResult, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            double maxArea = 0;
            int maxAreaIndex = -1;

            for (int i = 0; i < contours.size(); i++) {
                double area = Imgproc.contourArea(contours.get(i));
                if (area > maxArea) {
                    maxArea = area;
                    maxAreaIndex = i;
                }
            }

            if (maxAreaIndex == -1) return null;

            Rect rect = Imgproc.boundingRect(contours.get(maxAreaIndex));
            Imgproc.rectangle(screenshotMat, rect, new Scalar(0, 0, 0));
            Imgcodecs.imwrite("/private/var/folders/nk/b888bwnj5z365rf5k0gkk8x00000gn/T/test.png", screenshotMat);

            return new Point(rect.x + (rect.width / 2), rect.y + (rect.height / 2));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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