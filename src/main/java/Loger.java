import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class Loger extends Thread {

    private final Monitor mMonitor;
    private final PN mPN;
    private final List<Thread> threadList;

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

        printInvariants(pw);

        pw.printf("Buffers Loads: B1: %d  B2: %d\n",
                mPN.getPlaceTokens(PN.Places.Buffer1),
                mPN.getPlaceTokens(PN.Places.Buffer2));

        pw.printf("Threads States:\n");
        for (Thread c: threadList) {
            pw.printf(" Name: %s\tState: %s\n", c.getName(), c.getState());
        }

        pw.printf("\n%s", last? " \nExecution completed in less than "+each*i+" seconds." : "");
    }
    private void printInvariants(PrintWriter pw){
        pw.printf("Invariants: \n");
        //1
        pw.printf("P0(%d) + P1(%d) = 1\n", mPN.getPlaceTokens(PN.Places.P0), mPN.getPlaceTokens(PN.Places.P1));
        //2
        pw.printf("c1-idle(%d) + core1-active(%d) = 1\n", mPN.getPlaceTokens(PN.Places.c1_idle), mPN.getPlaceTokens(PN.Places.core1_active));
        pw.printf("c2-idle(%d) + core2-active(%d) = 1\n", mPN.getPlaceTokens(PN.Places.c2_idle), mPN.getPlaceTokens(PN.Places.core2_active));
        //3
        pw.printf("CPU1_ON(%d) + CPU1-Power_up(%d) + CPU1-Stand_by(%d) = 1\n", mPN.getPlaceTokens(PN.Places.CPU1_ON), mPN.getPlaceTokens(PN.Places.CPU1_PowerUp), mPN.getPlaceTokens(PN.Places.CPU1_StandBy));
        pw.printf("CPU2_ON(%d) + CPU2-Power_up(%d) + CPU2-Stand_by(%d) = 1\n", mPN.getPlaceTokens(PN.Places.CPU2_ON), mPN.getPlaceTokens(PN.Places.CPU2_PowerUp), mPN.getPlaceTokens(PN.Places.CPU2_StandBy));
    }

    public void stop(int elapsedTime) {
        this.interrupt();
    }
}

