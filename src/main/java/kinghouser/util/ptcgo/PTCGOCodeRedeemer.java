package kinghouser.util.ptcgo;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class PTCGOCodeRedeemer extends Thread {

    public AtomicBoolean running = new AtomicBoolean(true);

    public ArrayList<String> ptcgoCodeQueue = new ArrayList<>();

    public ArrayList<String> submittedCodes = new ArrayList<>();

    public void run() {
        while (running.get()) {
            if (ptcgoCodeQueue.size() != 0) {
                ArrayList<String> codes = ptcgoCodeQueue;
                for (String s : codes) {
                    if (!submittedCodes.contains(s)) {
                        PTCGOUtils.applyPTCGOCode(s);
                        submittedCodes.add(s);
                    }
                }
                ptcgoCodeQueue.clear();
            }
        }
    }
}