import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class Loger extends Thread {

    private Monitor mMonitor;
    private PN mPN;
    private List<Thread> threadList;

    private FileWriter file;
    private PrintWriter pw;

    public Loger(Monitor mMonitor, List<Thread> threadList, PN mPN,String fileLocation) {
        this.mMonitor = mMonitor;
        this.mPN = mPN;
        this.threadList = threadList;

        try {
            file = new FileWriter(fileLocation);
            pw = new PrintWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int each = 2; //how many seconds between iterations
        int i = 0;
        try{
            while(true){
                //Log
                log(each, i);

                //Sleep 'each' seconds
                Thread.sleep(each * 1000);
                i++;
            }
        } catch (InterruptedException e) {
            System.out.println("Loger detenido.");
            log(each, i, true);
        } finally {
            //Closing file
            try {
                file.close();
                pw.close();
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void log(int each, int i) { log(each, i, false); }

    private void log(int each, int i, boolean last) {
        i += last? 1 : 0;
        pw.printf("________________________________\n");
        pw.printf("Time:%s %d seconds.\n", last? " just under" : "", each*i);

        pw.printf("\n");

        pw.printf("Petri Net Marking: %s\n", mPN.getMarkingString());

        pw.printf("\n");

        pw.printf("Buffers Loads:\n");

        int b = 0;

        pw.printf("Threads States:\n");
        for (Thread c: threadList) {
            pw.printf(" Name: %s\tState: %s\n", c.getName(), c.getState());
        }

        pw.printf("\n%s", last? " \nExecution completed in less than "+each*i+" seconds." : "");
    }
}
