package fr.isen.twinmx.async;

import android.os.AsyncTask;

import com.github.mikephil.charting.data.Entry;

import java.util.LinkedList;

import fr.isen.twinmx.fragments.RealTimeChartComponent;
import fr.isen.twinmx.model.RawData;
import fr.isen.twinmx.model.RawMeasures;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothDataManager;

/**
 * Created by Clement on 20/01/2017.
 */

public class RawDataManagerAsyncTask extends AsyncTask<Void, Entry, Void> {

    private static int HEADER = 128;
    private final LinkedList<Integer> frames;
    private final RawData raw;
    private final RawMeasures rawMeasures;
    private final RealTimeChartComponent chart;
    private final TMBluetoothDataManager dataManager;
    private int x = 0;
    private int nbResults = 0;

    private boolean stop;

    public RawDataManagerAsyncTask(TMBluetoothDataManager dataManager, RealTimeChartComponent chart) {
        this.dataManager = dataManager;
        this.frames = dataManager.getFrames();
        this.chart = chart;
        this.raw = new RawData(); //needs msb + lsb
        this.rawMeasures = new RawMeasures(4); //needs 4 measures to be completed
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        stop = false;
        this.dataManager.startListening();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int frame;
        while (!stop) {
            if (!this.frames.isEmpty()) {
                synchronized (this.frames) {
                    frame = this.frames.pop();
                }
                try {
                    if (!resetRawData(frame) && this.raw.add(frame) && this.rawMeasures.add(this.raw.popMeasure())) { //not header frame && raw completed (msb + lsb) && 4 measures
                        addMeasures(this.rawMeasures.toEntries(x));
                    }
                }
                catch(IndexOutOfBoundsException ex) {
                    //Missed a HEADER frame
                    addMeasures(this.rawMeasures.toEntries(x));
                }
            }
        }
        return null;
    }

    private boolean resetRawData(int frame) {
        if (frame == HEADER) {
            clearAll();
            if (x < 200) {
                x++;
            }
            return true;
        }
        return false;
    }

    private void clearAll() {
        this.rawMeasures.clear();
        this.raw.clear();
    }

    private void addMeasures(Entry... entries) {
        chart.addEntries(entries);
        nbResults++;
        if (nbResults > 120) {
            publishProgress();
            nbResults = 0;
        }
    }

    @Override
    protected void onProgressUpdate(Entry... entries) {
        /*chart.addEntries(entries);
        nbResults++;
        if (nbResults > 20) {
            chart.refreshChart();
            nbResults = 0;
        }*/
        chart.refreshChart();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        stop = true;
        //TODO: Tell fragment the async task ended
        this.dataManager.stopListening();
    }

    public void stop() {
        stop = true;
    }
}
