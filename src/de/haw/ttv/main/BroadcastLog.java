package de.haw.ttv.main;

import de.uniba.wiai.lspi.chord.data.ID;

/**
 * stores the broadcasts we received. It is just a helper class with constructor and getter/setter
 */
public class BroadcastLog {

    private ID source;
    private ID target;
    private boolean hit;
    private int transactionID;

    public BroadcastLog(ID source, ID target, Boolean hit, int transactionID) {
        this.source = source;
        this.target = target;
        this.hit = hit;
        this.transactionID = transactionID;
//        System.out.println(this);
    }

    public ID getTarget() {
        return target;
    }

    public void setTarget(ID target) {
        this.target = target;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public ID getSource() {
        return source;
    }

    public void setSource(ID source) {
        this.source = source;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    @Override
    public String toString() {
        return "BroadcastLog [source=" + source + ", target=" + target + ", hit=" + hit + ", transactionID="
                + transactionID + "]";
    }

}
