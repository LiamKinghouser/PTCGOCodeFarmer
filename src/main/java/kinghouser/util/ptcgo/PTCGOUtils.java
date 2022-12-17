package kinghouser.util.ptcgo;

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Robot r = null;
        try {
            r = new Robot();
            r.delay(500);
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(0);
        }
        r.mouseMove(300, 700);
        r.delay(300);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        applescriptCommand =  "tell application \"System Events\"\n" +
                "keystroke \"" + code + "\"\n" +
                "end tell";

        String[] keystrokeArgs = { "osascript", "-e", applescriptCommand };
        try {
            Runtime.getRuntime().exec(keystrokeArgs);
            r.delay(500);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        r.mouseMove(320, 800);
        r.delay(100);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        r.delay(500);

        r.mouseMove(900, 800);
        r.delay(100);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        r.delay(500);

        r.mouseMove(600, 800);
        r.delay(100);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}