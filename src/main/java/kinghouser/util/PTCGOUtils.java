package kinghouser.util;

import java.awt.*;
import java.awt.event.InputEvent;

public class PTCGOUtils {

    public static void applyPTCGOCode(String code) {
        String applescriptCommand =  "tell application \"System Events\"\n" +
                "set frontmost of process \"Pokemon Trading Card Game Online\" to true\n" +
                "end tell";

        String[] args = { "osascript", "-e", applescriptCommand };
        try {
            Runtime.getRuntime().exec(args);
            Thread.sleep(100);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(0);
        }
        r.mouseMove(300, 400);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        applescriptCommand =  "tell application \"System Events\"\n" +
                "keystroke \"" + code + "\"\n" +
                "end tell";

        String[] keystrokeArgs = { "osascript", "-e", applescriptCommand };
        try {
            Runtime.getRuntime().exec(keystrokeArgs);
            Thread.sleep(100);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}