package fr.isen.twinmx.fragments;

import android.os.AsyncTask;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import fr.isen.twinmx.fragments.chart.RealTimeChartComponent;
import fr.isen.twinmx.model.MeasureEntry;
import fr.isen.twinmx.model.MeasuresList;
import fr.isen.twinmx.utils.bluetooth.TMBluetoothManager;

/**
 * Created by Clement on 19/01/2017.
 */

public class LineAsyncTask extends AsyncTask<Integer, MeasureEntry, Void> {

    private final RealTimeChartComponent chartComponent;
    private MeasuresList data;
    private boolean stop = false;

    private int nbResults = 0;

    public LineAsyncTask(RealTimeChartComponent chartComponent, TMBluetoothManager manager) {
        this.chartComponent = chartComponent;
        /*this.data = manager.getDataManager().getData();*/
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        Entry entry;
        int i = 0;
        while (!stop) {
            entry = this.data.tryPopFirst(i);
            if (entry != null) {
                publishProgress(new MeasureEntry(i, entry));
                Log.d("i","i:"+i);
                i = (i+1) % 4;
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(MeasureEntry... values) {
        int i = 0;
        for (MeasureEntry value : values) {
            this.chartComponent.addEntry(value.getLineIndex(), value.getValue());
            i = (i + 1) % 4;
        }
        if (++nbResults >= 120) {
            this.chartComponent.refreshChart();
            nbResults = 0;
        }
    }

    public void stop() {
        this.stop = true;
    }
}
