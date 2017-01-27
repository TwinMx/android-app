package fr.isen.twinmx.util.Bluetooth;

import java.util.List;
import fr.isen.twinmx.model.LimitedLinkedList;

/**
 * Created by Clement on 19/01/2017.
 */

public class TMBluetoothDataManager {

    private final List<Integer> frames;
    private int listeners = 0;

    public TMBluetoothDataManager(int size) {
        this.frames = new LimitedLinkedList<>(size);
    }

    public void startListening() {
        listeners++;
    }

    public void stopListening() {
        listeners = listeners > 0 ? listeners - 1 : 0;
        if (listeners <= 0) {
            frames.clear();
        }
    }

    public void addFrame(int frame) {
        if (listeners > 0) {
            synchronized (this.frames) {
                frames.add(frame);
            }
        }
    }

    public List<Integer> getFrames() {
        return frames;
    }
}
