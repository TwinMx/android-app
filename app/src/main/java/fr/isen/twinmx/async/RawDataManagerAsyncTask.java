package fr.isen.twinmx.async;

import android.os.AsyncTask;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import fr.isen.twinmx.fragments.chart.RealTimeChartComponent;
import fr.isen.twinmx.model.RawData;
import fr.isen.twinmx.model.RawMeasures;
import fr.isen.twinmx.utils.bluetooth.TMBluetoothDataManager;

/**
 * Created by Clement on 20/01/2017.
 */

public class RawDataManagerAsyncTask extends StoppableAsyncTask<Void, Entry, Void> {

    private static int HEADER = 128;
    private static final int AVERAGE = 4;
    private int nbPointsInAverage = 0;
    private float[] sum = new float[]{0, 0, 0, 0};

    private final List<Integer> frames;
    private final RawData raw;
    private final RawMeasures rawMeasures;
    private final RealTimeChartComponent chart;
    private final TMBluetoothDataManager dataManager;
    private int x = 0;
    private int nbResults = 0;

    private static final int REFRESH_RATE = 50; //200;

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
        this.dataManager.startListening();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int frame;
        while (!hasToStop()) {
            synchronized (this.frames) {
                if (!this.frames.isEmpty()) {
                    synchronized (this.frames) {
                        frame = this.frames.remove(0);
                    }
                    try {
                        if (!resetRawData(frame) && this.raw.add(frame) && this.rawMeasures.add(this.raw.popMeasure())) { //not header frame && raw completed (msb + lsb) && 4 measures
                            addMeasures(this.rawMeasures.toEntries(x));
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        //Missed a HEADER frame
                        addMeasures(this.rawMeasures.toEntries(x));
                    }
                }
            }
        }
        this.dataManager.stopListening();
        return null;
    }

    private boolean resetRawData(int frame) {
        if (frame == HEADER) {
            clearAll();
            if (x < REFRESH_RATE) {
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
        for (int i = 0; i < entries.length; i++) {
            sum[i] += entries[i].getY();
        }
        nbPointsInAverage++;

        if (nbPointsInAverage >= AVERAGE) {
            chart.addEntries(new Entry(0, sum[0] / nbPointsInAverage), new Entry(0, sum[1] / nbPointsInAverage), new Entry(0, sum[2] / nbPointsInAverage), new Entry(0, sum[3] / nbPointsInAverage));
            for (int i = 0; i < sum.length; i++) {
                sum[i] = 0;
            }
            nbPointsInAverage = 0;
            nbResults++;
            if (nbResults > REFRESH_RATE) {
                if (!hasToStop()) {
                    publishProgress();
                    nbResults = 0;
                }
            }
        }
    }

    @Override
    protected void onProgressUpdate(Entry... entries) {
        chart.refreshChart();
    }

    @Override
    public void stop() {
        super.stop();
        this.dataManager.stopListening();
    }
}
