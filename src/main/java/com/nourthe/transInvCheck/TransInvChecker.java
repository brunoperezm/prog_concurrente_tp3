package com.nourthe.transInvCheck;

import java.util.List;

public class TransInvChecker {

    /**
     * @param transInvs List of Transition Invariants(ArrayLists)
     * @param file Location of the file with the transitions sequence
     * @return
     */
    public static <E extends Enum<E>> List<E> checkTransitions(List<TInvariant<E>> transInvs, String file){
        List<E> transitionsSequence = (List<E>) FileTransitionsReader.read(file);
        return checkTransitions(transInvs, transitionsSequence);
    }

    /**
     * @param transInvs List of Transition Invariants(ArrayLists)
     * @param sequence Sequence of transitions to check
     * @return
     */
    public static <E extends Enum<E>> List<E> checkTransitions(List<TInvariant<E>> transInvs, List<E> sequence){
        if(transInvs.size() == 0){
            System.out.println("WARNING: There are not transition invariants.");
        }
        System.out.printf("Sequence of %d transitions obtained\n", sequence.size());

        while( sequence.size() > 0){
            TInvariant<E> tInvFound = new TInvariant<>();

            int i = search(transInvs, sequence, tInvFound);

            if (i == -1){ return sequence; }
            else{
                reverseDelete(tInvFound, sequence, i);

                for (TInvariant tInv : transInvs){
                    tInv.reset();
                }
            }

        }
        return sequence;
    }

    private static <E extends Enum<E>> int search(List<TInvariant<E>> transInvs, List<E> transitionsSequence, TInvariant<E> tInvFound) {
        for (int i=0; i<transitionsSequence.size(); i++){
            for( TInvariant tInv : transInvs){
                if(transitionsSequence.get(i) == tInv.getCurrent()){
                    if(tInv.hasNext()){
                        tInv.next();
                    }
                    else{
                        tInv.found();
                        tInvFound.addAll(tInv.getTransitions());
                        tInvFound.setP(tInv.getP());
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private static <E extends Enum<E>> void reverseDelete(TInvariant<E> tInvFound, List<E> transitionsSequence, int p) {
        int i = p;

        while ( i >= 0 ){
            try {
                if (transitionsSequence.get(i) == tInvFound.getCurrent()) {
                    tInvFound.previous(); // I step back
                    transitionsSequence.remove(i); // I delete de Transition corresponding to the invariant
                }
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
            i--;
        }
    }

}
