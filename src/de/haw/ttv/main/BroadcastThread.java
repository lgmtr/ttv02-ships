package de.haw.ttv.main;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.Chord;

/**
 * makes shots run in separate threads to prevent thread limit from chord.properties
 */
public class BroadcastThread extends Thread {

    private final ID target;
    private final Boolean hit;
    private final Chord chord;

    BroadcastThread(Chord chord, ID target, Boolean hit) {
        this.chord = chord;
        this.target = target;
        this.hit = hit;
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                this.wait(300);
                chord.broadcast(target, hit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
