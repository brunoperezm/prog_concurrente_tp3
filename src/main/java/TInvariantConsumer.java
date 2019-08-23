import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class TInvariantConsumer {

    private LinkedList<String> T1;
    private LinkedList<String> T2;
    private LinkedList<String> T3;
    private LinkedList<String> T4;
    private CopyOnWriteArrayList<String> lines;
    private List<LinkedList<String>> TInvariantsList;
    private LinkedList<String> buffer;

    public TInvariantConsumer() {
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


        try {
            lines = new CopyOnWriteArrayList<>(Files.readAllLines(Paths.get("out/transitions.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void refillInvariants(LinkedList<String> invariant){
        T1.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_1", "START_SERVICE_1", "END_SERVICE_RATE_1", "ZT19", "CONSUME_PENDING_TASK_TOKEN_1", "ZT17"));
        T2.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_1", "WAKE_UP_1", "RETURN_PENDING_TASK_1", "POWER_UP_DELAY_1", "START_SERVICE_1", "ZT19", "END_SERVICE_RATE_1", "POWER_DOWN_THRESHOLD_1"));
        T3.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_2", "START_SERVICE_2", "END_SERVICE_RATE_2", "ZT20", "CONSUME_PENDING_TASK_TOKEN_2", "ZT18"));
        T4.addAll(Arrays.asList("ARRIVAL_RATE", "START_BUFFER_2", "WAKE_UP_2", "RETURN_PENDING_TASK_2", "POWER_UP_DELAY_2", "START_SERVICE_2", "ZT20", "END_SERVICE_RATE_2", "POWER_DOWN_THRESHOLD_2"));
    }


    public void mainCheck(){

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
    }


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



}
