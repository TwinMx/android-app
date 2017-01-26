package fr.isen.twinmx.util.Bluetooth;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import fr.isen.twinmx.model.MeasuresList;
import fr.isen.twinmx.model.RawMeasures;
import fr.isen.twinmx.model.RawData;

/**
 * Created by Clement on 19/01/2017.
 */

public class TMBluetoothDataManager {

    private final List<Integer> frames = new LinkedList<>();
    private int waitingThreads = 0;

    private int listeners = 0;

    public void startListening() {
        listeners++;
    }

    public void stopListening() {
        listeners = listeners > 0 ? listeners - 1 : 0;
    }

    public void addFrame(int frame) {
        if (listeners > 0) {
            synchronized (this.frames) {
                frames.add(frame);
                if (waitingThreads > 0) {
                    this.frames.notifyAll();
                    waitingThreads = 0;
                }
            }
        }
    }

    public void waitThread() {
        waitingThreads++;
    }

    public List<Integer> getFrames() {
        return frames;
    }


    /*private static int HEADER = 128;*/


/*    private MeasuresList data = new MeasuresList(500, 4);

    private RawData raw = new RawData();
    private RawMeasures rawMeasures = new RawMeasures(4);*/


/*    public MeasuresList getData() {
        return data;
    }

    public void addRawData(int item) {
        if (stop) return;

        if (!resetRawData(item)) {
            this.raw.add(item);
            if (this.raw.isComplete()) {
                addRawMeasure(this.raw.popMeasure());
            }
        }
    }*/

    /**
     * Checks if the header (128) is received : Initializes for a new series of rawMeasures (clears the raw results)
     * @param item
     * @return true if header was received
     */
    /*private boolean resetRawData(int item) {
        if (item == HEADER) {
            this.rawMeasures.clear();
            return true;
        }
        return false;
    }



    public void addRawMeasure(double measure) {
        this.rawMeasures.add(measure);
        if (this.rawMeasures.isComplete()) {
            addData(this.rawMeasures.getValues());
            resetRawMeasure();
        }
    }

    private void resetRawMeasure() {
        this.rawMeasures.clear();
    }

    private void addData(int[] items) {
        this.data.add(items);
        notifyDataReceived(items);
    }

    private void notifyDataReceived(int[] values) {
        this.setChanged();
        this.notifyObservers();
    }

    private boolean stop = false;

    public void stop() {
        stop = true;
        raw.clear();
        rawMeasures.clear();
        data.clear();
    }

    public void start() {
        stop = false;
    }*/
}
