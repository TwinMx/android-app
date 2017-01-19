package fr.isen.twinmx.util.Bluetooth;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import fr.isen.twinmx.model.Measures;
import fr.isen.twinmx.model.RawMeasure;

/**
 * Created by Clement on 19/01/2017.
 */

public class TMBluetoothDataManager extends Observable {

    private static int HEADER = 128;


    private List<int[]> data = new LinkedList<>();

    private RawMeasure raw = new RawMeasure();
    private Measures measures = new Measures(4);


    public List<int[]> getData() {
        return this.data;
    }

    public void addRawData(int item) {
        if (!resetRawData(item)) {
            this.raw.add(item);
            if (this.raw.isComplete()) {
                addRawMeasure(this.raw.popMeasure());
            }
        }
    }

    /**
     * Checks if the header (128) is received : Initializes for a new series of measures (clears the raw results)
     * @param item
     * @return true if header was received
     */
    private boolean resetRawData(int item) {
        if (item == HEADER) {
            this.measures.clear();
            return true;
        }
        return false;
    }



    public void addRawMeasure(double measure) {
        this.measures.add(measure);
        if (this.measures.isComplete()) {
            addData(this.measures.getValues());
            resetRawMeasure();
        }
    }

    private void resetRawMeasure() {
        this.measures.clear();
    }

    private void addData(int[] items) {
        this.data.add(items);
        notifyDataReceived(items);
    }

    private synchronized void notifyDataReceived(int[] values) {
        this.setChanged();
        this.notifyObservers();
    }
}
