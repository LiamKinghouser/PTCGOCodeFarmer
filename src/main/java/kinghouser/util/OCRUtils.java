package kinghouser.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class OCRUtils {

    public static void checkVideo(File file) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file);
            Java2DFrameConverter converter = new Java2DFrameConverter();
            checkImages(grabber, converter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkImages(FFmpegFrameGrabber grabber, Java2DFrameConverter converter) {
        try {
            ArrayList<String> results = new ArrayList<>();

            grabber.start();
            int totalFrames = grabber.getLengthInFrames();

            // reusing variables to increase speed
            // not 100% sure if working but time was reduced by 17 seconds in a test case (44 fps -> 46 fps)
            // (versus creating new vars each time image checked)

            MultiFormatReader multiFormatReader = new MultiFormatReader();
            LuminanceSource luminanceSource = null;
            BinaryBitmap bitmap = null;
            Result r = null;

            System.out.println("Video length: " + grabber.getLengthInTime() + " seconds");
            System.out.println("Starting scan for " + totalFrames + " frames...");
            double startTime = System.currentTimeMillis();
            for (int i = 1; i <= totalFrames; i++) {
                Frame frame = grabber.grabImage();
                BufferedImage bi = converter.convert(frame);
                if (bi == null) continue;

                String result = decodeQRCode(bi, multiFormatReader, luminanceSource, bitmap, r);

                if (result != null && !result.isBlank() && !results.contains(result) && Utils.isPTCGOCode(result)) results.add(result);

                System.out.print("\r");
                System.out.print("[ " + (int)(((float)i / (float)totalFrames) * 100) + "% ] [ " + i + "/" + totalFrames + " ] [ " + Utils.findAverageSpeed(i, System.currentTimeMillis() - startTime) + " fps ] [ " + ((System.currentTimeMillis() - startTime) / 1000) + " ]");
            }
            grabber.stop();
            System.out.println();
            System.out.println("Done. Time elapsed: " + ((System.currentTimeMillis() - startTime) / 1000));
            System.out.println("Found " + results.size() + " QR Codes:");
            for (String result : results) {
                System.out.println(result);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
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