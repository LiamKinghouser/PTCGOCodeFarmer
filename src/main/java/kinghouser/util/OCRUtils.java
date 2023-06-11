package kinghouser.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import kinghouser.PTCGOCodeFarmer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class OCRUtils {

    public static boolean checkVideo(File file, long threadID) {
        if (file == null) return false;
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file);
            Java2DFrameConverter converter = new Java2DFrameConverter();
            return checkImages(file, grabber, converter, threadID);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkImages(File file, FFmpegFrameGrabber grabber, Java2DFrameConverter converter, long threadID) {
        try {
            ArrayList<String> results = new ArrayList<>();

            grabber.start();
            int totalFrames = grabber.getLengthInFrames();

            MultiFormatReader multiFormatReader = new MultiFormatReader();

            System.out.println("Starting scan for " + totalFrames + " frames...");
            long startTime = System.currentTimeMillis();
            for (int i = 1; i <= totalFrames; i++) {
                Frame frame = grabber.grabImage();
                BufferedImage bi = converter.convert(frame);

                int index = PTCGOCodeFarmer.youTubeCrawler.getIndex(threadID);
                PTCGOCodeFarmer.guiThread.updateImage(index, bi);

                if (bi == null) {
                    continue;
                }

                String result = decodeQRCode(bi, multiFormatReader, null, null, null);

                if (result != null && !result.isBlank() && !results.contains(result) && Utils.isPTCGOCode(result)) {
                    results.add(result);
                    PTCGOCodeFarmer.ptcgoCodeRedeemer.addCodeToQueue(result);
                    System.out.println(result);
                }

                // System.out.print("\r");
                // System.out.print("[ " + (int)(((float)i / (float)totalFrames) * 100) + "% ] [ " + i + "/" + totalFrames + " ] [ " + Utils.findAverageSpeed(i, System.currentTimeMillis() - startTime) + " fps ] [ " + Utils.getTime((int) ((System.currentTimeMillis() - startTime) / 1000)) + " ]");
            }
            grabber.stop();
            // System.out.print("\r");
            // System.out.print("[ 100% ] [ " + totalFrames + "/" + totalFrames + " ] [ " + Utils.findAverageSpeed(totalFrames, System.currentTimeMillis() - startTime) + " fps ]");

            // System.out.println();
            // System.out.println("Frames scanned. Time elapsed: " + Utils.getTime((int)(System.currentTimeMillis() - startTime) / 1000));
            // System.out.println("Found " + results.size() + " QR Codes:");
            file.delete();
            PTCGOCodeFarmer.guiThread.resetImage(PTCGOCodeFarmer.youTubeCrawler.getIndex(threadID));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static String decodeQRCode(BufferedImage bufferedImage, MultiFormatReader multiFormatReader, LuminanceSource source, BinaryBitmap bitmap, Result result) {
        source = new BufferedImageLuminanceSource(bufferedImage);
        bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            result = multiFormatReader.decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return "";
        }
    }
}