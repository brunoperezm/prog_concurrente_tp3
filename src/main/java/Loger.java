import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class Loger extends Thread {

    private final PN mPN;
    private final List<Thread> threadList;

    private FileWriter file;
    private PrintWriter pw;
    private int logRate = Main.LOG_RATE_MILLISECONDS; //how many milliseconds between iterations

    Loger(List<Thread> threadList, PN mPN, String fileLocation) {
        this.mPN = mPN;
        this.threadList = threadList;

        try {
            file = new FileWriter(fileLocation);
            pw = new PrintWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss '-' yyyy-MM-dd ");
        pw.println("Inicio del log - " + sdf.format(new Date()));
    }

    @Override
    public void run() {
        int i = 0;
        try{
            while(true){
                //Log
                log(logRate, i);

                //Sleep 'logRate' seconds
                Thread.sleep(logRate);
                i++;
            }
        } catch (InterruptedException e) {
            System.out.println("Loger detenido.");
            log(logRate, i, true);
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
        pw.printf("Time:%s %d milliseconds.\n", last? " just under" : "", each*i);

        pw.printf("\n");

        pw.printf("Petri Net Marking: %s\n", mPN.getMarkingString());

        pw.printf("\n");

        printInvariants(pw);

        pw.printf("Buffers Loads: B1: %d  B2: %d\n",
                mPN.getPlaceTokens(PN.Places.CORE1_BUFFER),
                mPN.getPlaceTokens(PN.Places.CORE2_BUFFER));

        pw.printf("Threads States:\n");
        for (Thread c: threadList) {
            pw.printf(" Name: %s\tState: %s\n", c.getName(), c.getState());
        }

        pw.printf("\n%s", last? " \nSe completó la ejecución en "+each*i+" milisegundos." : "");
    }
    private void printInvariants(PrintWriter pw){
        pw.printf("Invariants: \n");
        //1
        pw.printf("P0(%d) + P1(%d) = 1\n", mPN.getPlaceTokens(PN.Places.P0), mPN.getPlaceTokens(PN.Places.P1));
        //2
        pw.printf("c1-idle(%d) + core1-active(%d) = 1\n", mPN.getPlaceTokens(PN.Places.C1_IDLE), mPN.getPlaceTokens(PN.Places.CORE1_ACTIVE));
        pw.printf("c2-idle(%d) + core2-active(%d) = 1\n", mPN.getPlaceTokens(PN.Places.C2_IDLE), mPN.getPlaceTokens(PN.Places.CORE2_ACTIVE));
        //3
        pw.printf("CPU1_ON(%d) + CPU1-Power_up(%d) + CPU1-Stand_by(%d) = 1\n", mPN.getPlaceTokens(PN.Places.CPU1_ON), mPN.getPlaceTokens(PN.Places.CPU1_POWER_UP), mPN.getPlaceTokens(PN.Places.CPU1_STAND_BY));
        pw.printf("CPU2_ON(%d) + CPU2-Power_up(%d) + CPU2-Stand_by(%d) = 1\n", mPN.getPlaceTokens(PN.Places.CPU2_ON), mPN.getPlaceTokens(PN.Places.CPU2_POWER_UP), mPN.getPlaceTokens(PN.Places.CPU2_STAND_BY));
    }

}

