package fr.isen.twinmx.fragments;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import fr.isen.twinmx.R;
import fr.isen.twinmx.async.RawDataManagerAsyncTask;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothListener;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;

/**
 * Created by Clement on 19/01/2017.
 */

public class RealTimeChartComponent implements Observer, OnChartGestureListener, OnChartValueSelectedListener {

    private static int NB_POINTS = 200;
    private final Activity context;
    private final LineChart mChart;
    private ArrayList<LimitedEntryList> dataSetEntries = new ArrayList<>(4);
    private TMBluetoothListener listener;
    private RawDataManagerAsyncTask rawDataManagerAsyncTask;

    public RealTimeChartComponent(Activity context, LineChart chart) {
        this.context = context;
        this.mChart = chart;
    }

    /** onCreate **/
    public void onCreate() {
        mChart.setData(new LineData());
        initChartSettings();
    }

    private void initChartSettings() {
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setDrawLabels(false);
        for(int index = 0; index < 4; index++) {
            dataSetEntries.add(null);
        }
        mChart.setDrawGridBackground(false);
        mChart.setDescription(new Description() {{
            setText("Pression (mBar)");
        }});
        mChart.getLegend().setEnabled(false);

    }

    /** onResume() **/
    public void onResume() {
        this.listener = TMBluetoothManager.getInstance().getBluetooth().getListener();
        listener.addObserver(this);
        update();
    }


    public void update() {
        if (listener.getConnectedDevice() != null) { //If there's a connected device
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
            LimitedEntryList entries = this.dataSetEntries.get(index);
            if (entries == null) {
                entries = addNewSet(this.context.getString(R.string.cylinder, index + 1), index);
                this.dataSetEntries.set(index, entries);
            }

            synchronized(entries) {
                data.addEntry(value, index);
                //entries.add(value.getY());
            }
            data.notifyDataChanged();
        }
    }

    public void refreshChart() {
        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();

/*        // limit the number of visible entries
        mChart.setVisibleXRangeMaximum(NB_POINTS);

        // move to the latest entry
        mChart.moveViewToX(mChart.getData().getEntryCount());
        // this automatically refreshes the chart (calls invalidate())*/
        mChart.invalidate();
    }

    private LimitedEntryList addNewSet(String title, int index) {

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

        LimitedEntryList entries = new LimitedEntryList(NB_POINTS);

        LineDataSet dataSet = new LineDataSet(entries, title);
        dataSet.setColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(0);
        dataSet.setValueTextColor(color);
        dataSet.setCircleColor(color);

        mChart.getData().addDataSet(dataSet);
        return entries;
    }


    public void play() {
        if (rawDataManagerAsyncTask != null) {
            rawDataManagerAsyncTask.stop();
            rawDataManagerAsyncTask = null;
        }
        for(LimitedEntryList entries : this.dataSetEntries) {
            if (entries != null) {
                entries.reset();
            }
        }
        rawDataManagerAsyncTask = new RawDataManagerAsyncTask(TMBluetoothManager.getInstance().getDataManager(), this);
        rawDataManagerAsyncTask.execute();
    }

    public void pause() {
        if (rawDataManagerAsyncTask != null) {
            rawDataManagerAsyncTask.stop();
        }
        rawDataManagerAsyncTask = null;
    }


    @Override
    public void update(Observable observable, Object o) {
        update();
    }

    public void setVisible(Integer index, boolean checked) {
        this.mChart.getLineData().getDataSetByIndex(index).setVisible(checked);
        refreshChart();
    }

    public void fitScreen() {
        mChart.fitScreen();
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
