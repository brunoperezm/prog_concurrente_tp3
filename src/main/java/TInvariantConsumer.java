import com.nourthe.transInvCheck.TInvariant;
import com.nourthe.transInvCheck.TransInvChecker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class TInvariantConsumer {

    /* Código viejo de Fran
    private LinkedList<String> T1;
    private LinkedList<String> T2;
    private LinkedList<String> T3;
    private LinkedList<String> T4;
    private List<LinkedList<String>> TInvariantsList;
    private LinkedList<String> buffer;
     */
    private CopyOnWriteArrayList<String> lines;

    ArrayList<TInvariant<PN.Transitions>> tinvs = new ArrayList<>();
    List<PN.Transitions> seq = new ArrayList<>();
    private boolean verbose = true;

    public TInvariantConsumer() {
        /**
         * Este hermoso algoritmo no sirve porque nourthe/pnet-tinvcheck funciona
         * con las transiciones en orden. Quedará acá porque al menos puede servir
         * para copiar las transiciones del output de java, aunque hay que ordenarlas.
         *
        int[][] tInvMatrix = {
            {1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0},
            {1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0},
            {1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
            {1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1}
        };
        ArrayList<TInvariant<PN.Transitions>> tinvs = new ArrayList<>();

        for(int i=0; i<tInvMatrix.length; i++){
            tinvs.add(new TInvariant<>());
            for(int j=0; j<tInvMatrix[i].length; j++){
                if(tInvMatrix[i][j]==1){
                    PN.Transitions trans = PN.Transitions.values()[j];
                    System.out.print(trans + ", ");
                    tinvs.get(i).add(  trans);
                }
            }
            System.out.println("");
        } */

        for(int i=0; i<4; i++){
            tinvs.add(new TInvariant<>());
        }

        tinvs.get(0).addAll(Arrays.asList(
                PN.Transitions.ARRIVAL_RATE,
                PN.Transitions.START_BUFFER_1,
                PN.Transitions.CONSUME_PENDING_TASK_TOKEN_1,
                PN.Transitions.START_SERVICE_1,
                PN.Transitions.END_SERVICE_RATE_1
                )) ;
        tinvs.get(1).addAll(Arrays.asList(
                PN.Transitions.ARRIVAL_RATE,
                PN.Transitions.START_BUFFER_2,
                PN.Transitions.CONSUME_PENDING_TASK_TOKEN_2,
                PN.Transitions.START_SERVICE_2,
                PN.Transitions.END_SERVICE_RATE_2
        )) ;
        tinvs.get(2).addAll(Arrays.asList(
                PN.Transitions.ARRIVAL_RATE,
                PN.Transitions.START_BUFFER_1,
                PN.Transitions.WAKE_UP_1,
                PN.Transitions.POWER_UP_DELAY_1,
                PN.Transitions.START_SERVICE_1,
                PN.Transitions.END_SERVICE_RATE_1,
                PN.Transitions.POWER_DOWN_THRESHOLD_1
        )) ;
        tinvs.get(3).addAll(Arrays.asList(
                PN.Transitions.ARRIVAL_RATE,
                PN.Transitions.START_BUFFER_2,
                PN.Transitions.WAKE_UP_2,
                PN.Transitions.POWER_UP_DELAY_2,
                PN.Transitions.START_SERVICE_2,
                PN.Transitions.END_SERVICE_RATE_2,
                PN.Transitions.POWER_DOWN_THRESHOLD_2
        )) ;

        /**
         * Código viejo de Fran
                T1 =  new LinkedList<>();
        T1.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_1", "START_SERVICE_1", "END_SERVICE_RATE_1", "ZT19", "CONSUME_PENDING_TASK_TOKEN_1", "ZT17"));
        T2 =  new LinkedList<>();
        T2.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_1", "WAKE_UP_1", "RETURN_PENDING_TASK_1", "POWER_UP_DELAY_1", "START_SERVICE_1", "ZT19", "END_SERVICE_RATE_1", "POWER_DOWN_THRESHOLD_1"));
        T3 =  new LinkedList<>();
        T3.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_2", "START_SERVICE_2", "END_SERVICE_RATE_2", "ZT20", "CONSUME_PENDING_TASK_TOKEN_2", "ZT18"));
        T4 =  new LinkedList<>();
        T4.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_2", "WAKE_UP_2", "RETURN_PENDING_TASK_2", "POWER_UP_DELAY_2", "START_SERVICE_2", "ZT20", "END_SERVICE_RATE_2", "POWER_DOWN_THRESHOLD_2"));

        TInvariantsList = new ArrayList<>();
        TInvariantsList.addAll(Arrays.asList(T1, T2, T3, T4));
        this.buffer = new LinkedList<>();
         */


        try {
            lines = new CopyOnWriteArrayList<>(Files.readAllLines(Paths.get("out/transitions.txt")));
            if (verbose) System.out.print(lines.size() + " transiciones leídas, ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String trans : lines){
            seq.add(PN.Transitions.valueOf(trans));
        }
        if (verbose) System.out.println(lines.size() + " transiciones convertidas correctamente.");

    }

    /**
     * Código viejo de Fran
    public void refillInvariants(LinkedList<String> invariant){
        T1.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_1", "START_SERVICE_1", "END_SERVICE_RATE_1", "ZT19", "CONSUME_PENDING_TASK_TOKEN_1", "ZT17"));
        T1.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_1", "START_SERVICE_1", "END_SERVICE_RATE_1", "ZT19", "CONSUME_PENDING_TASK_TOKEN_1", "ZT17"));
        T2.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_1", "WAKE_UP_1", "RETURN_PENDING_TASK_1", "POWER_UP_DELAY_1", "START_SERVICE_1", "ZT19", "END_SERVICE_RATE_1", "POWER_DOWN_THRESHOLD_1"));
        T3.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_2", "START_SERVICE_2", "END_SERVICE_RATE_2", "ZT20", "CONSUME_PENDING_TASK_TOKEN_2", "ZT18"));
        T4.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_2", "WAKE_UP_2", "RETURN_PENDING_TASK_2", "POWER_UP_DELAY_2", "START_SERVICE_2", "ZT20", "END_SERVICE_RATE_2", "POWER_DOWN_THRESHOLD_2"));
    }
     */


    public void mainCheck(){

        List<PN.Transitions> result = TransInvChecker.checkTransitions(tinvs, seq);
        if(verbose) System.out.println(result.size() + " sobrantes: " + result);
        for (TInvariant<PN.Transitions> tinv : tinvs){
            if(verbose) System.out.println(tinv.toString() + ": " + tinv.getCounter());
        }
        /**
         * Código viejo de Fran
        for (LinkedList<String> tInvariant: TInvariantsList) {

            try{
                this.lines.stream().forEachOrdered(s -> checkIfBelongToInvariant(s, tInvariant));

            }catch (NoSuchElementException e){

            }finally {
                this.lines.clear();
                this.lines.addAll(this.buffer);
                this.buffer.clear();
            }
        }

        try {
            Files.write(Paths.get("out/testDetector.txt"), this.lines, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        }
         */
    }

/**
 * Código viejo de Fran

 public void checkIfBelongToInvariant(String transition, LinkedList<String> TInvariant){
        if(transition.equals(TInvariant.getFirst())){
            try{
                TInvariant.remove();
            }
            catch(NoSuchElementException e){
                refillInvariants(TInvariant);
            }
        } else{
            this.buffer.add(transition);

        }

    }
 */



}
