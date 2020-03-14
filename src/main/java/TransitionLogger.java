import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TransitionLogger extends Thread {

    final ConcurrentLinkedQueue<String> transitionQueue = new ConcurrentLinkedQueue<>();
    private FileWriter file;
    private final String fileLocation;
    private PrintWriter pw;

    public TransitionLogger(String fileLocation) {
        this.fileLocation = fileLocation;
        this.setName(this.getClass().getSimpleName());
    }



    @Override
    public void run() {
        try {
            file = new FileWriter(fileLocation);
            pw = new PrintWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        while(!interrupted()) {
            String s = transitionQueue.poll();
            if (s != null) pw.printf(s+",");
            else {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    currentThread().interrupt();
                    try {
                        file.close();
                        pw.close();
                    }
                    catch (IOException j) { j.printStackTrace(); }
                }
            }
        }
    }

}
