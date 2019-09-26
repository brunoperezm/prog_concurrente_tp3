package com.nourthe.transInvCheck;

import java.util.ArrayList;
import java.util.List;

public class TInvariant<E extends Enum<E>> {

    // List of the Invariant transitions.
    private List<E> transitions;

    // Pointer to the current transition.
    private int p;
    // Found counter
    private int c;

    public TInvariant() {
        this.transitions = new ArrayList<>();
        setP(0);
        c = 0;
    }

    public List<E> getTransitions() {
        return transitions;
    }

    public int getP(){
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public void addAll(List<E> transitions){
        this.transitions.addAll(transitions);
    }

    public void add(E transition){
        this.transitions.add(transition);
    }

    public boolean hasNext(){
        return (p+1) < transitions.size();
    }

    public void next(){
        p++;
    }

    public void previous(){
        p--;
    }

    public E getCurrent(){
        if(transitions.size() > 0 && p>=0) {
            return transitions.get(p);
        }else{
            return null;
        }
    }

    public void reset(){
        p = 0;
    }

    public int getCounter(){ return c; }

    public void found(){ c++; }
}
