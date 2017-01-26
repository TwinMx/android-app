package fr.isen.twinmx.fragments;

import android.app.Activity;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import fr.isen.twinmx.R;
import fr.isen.twinmx.async.RawDataManagerAsyncTask;
import fr.isen.twinmx.model.EntriesLimitedLinkedList;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothListener;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;

/**
 * Created by Clement on 19/01/2017.
 */

public class RealTimeChartComponent implements Observer {

    private static int NB_POINTS = 200;
    private final Activity context;
    private final LineChart mChart;
    private ArrayList<LinkedList<Entry>> dataSetEntries = new ArrayList<>(4);
    private TMBluetoothListener listener;

    public RealTimeChartComponent(Activity context, LineChart chart) {
        this.context = context;
        mChart = chart;
    }

    private void initChart() {
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setDrawLabels(false);
        for(int index = 0; index < 4; index++) {
            dataSetEntries.add(null);
        }
    }

    public void onCreate() {
        mChart.setData(new LineData());
        initChart();
    }

    public void onResume() {
        this.listener = TMBluetoothManager.getInstance().getBluetooth().getListener();
        listener.addObserver(this);
        update();
    }

    public void update() {
        if (listener.getConnectedDevice() != null) {
            play();
        }
        else {
            pause();
        }
    }

    /**
     *
     * @param entries One entry per dataset
     */
    public void addEntries(Entry... entries) {
        int size = entries.length;
        for(int i = 0; i < size; i++) {
            addEntry(i, entries[i]);
        }
    }

    public void addEntry(int index, Entry value) {
        LineData data = mChart.getData();

        if (data != null) {
            LinkedList<Entry> entries = this.dataSetEntries.get(index);
            if (entries == null) {
                entries = addNewSet(this.context.getString(R.string.cylinder, index + 1), index);
            }

            synchronized(entries) {
                data.addEntry(value, index);
            }
            data.notifyDataChanged();
        }
    }

    public void refreshChart() {
        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();

        // limit the number of visible entries
        //mChart.setVisibleXRangeMaximum(NB_POINTS);
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        mChart.setVisibleXRangeMaximum(NB_POINTS);

        // move to the latest entry
        mChart.moveViewToX(mChart.getData().getEntryCount());
        //mChart.moveViewToX(mChart.getData().getEntryCount());

        // this automatically refreshes the chart (calls invalidate())
        // mChart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
    }

    private LinkedList<Entry> addNewSet(String title, int index) {

        int color = 0;
        switch (index) {
            case 0:
                color = R.color.chartBlue;
                break;
            case 1:
                color = R.color.chartGreen;
                break;
            case 2:
                color = R.color.chartBrown;
                break;
            case 3:
                color = R.color.chartRed;
                break;
            default:
                color = R.color.chartBlue;
                break;
        }

        color = ContextCompat.getColor(this.context, color);

        LinkedList<Entry> entries = new EntriesLimitedLinkedList(NB_POINTS, new Entry(0,0));

        LineDataSet dataSet = new LineDataSet(entries, title);
        dataSet.setColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(0);
        dataSet.setValueTextColor(color);
        dataSet.setCircleColor(color);

        mChart.getData().addDataSet(dataSet);
        this.dataSetEntries.set(index, entries);
        return entries;
    }

    private RawDataManagerAsyncTask a1;

    public void play() {
        if (a1 != null) {
            a1.stop();
        }
        mChart.getData().clearValues();
        a1 = new RawDataManagerAsyncTask(TMBluetoothManager.getInstance().getDataManager(), this);
        a1.execute();
    }

    public void pause() {
        if (a1 != null) {
            a1.stop();
        }
        a1 = null;
    }


    @Override
    public void update(Observable observable, Object o) {
        update();
    }
}
