package fr.isen.twinmx.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import fr.isen.twinmx.R;
import fr.isen.twinmx.async.RawDataManagerAsyncTask;
import fr.isen.twinmx.model.GraphDirection;
import fr.isen.twinmx.model.InitChartData;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

/**
 * Created by Clement on 19/01/2017.
 */

public class RealTimeChartComponent implements Observer, OnChartGestureListener, OnChartValueSelectedListener {

    public static int NB_POINTS = 200;
    private final Activity context;
    private final LineChart mChart;
    private final InitChartData initChartData;
    private ArrayList<LimitedEntryList> dataSetEntries = new ArrayList<>(4);
    private TMBluetooth mBluetooth;
    private RawDataManagerAsyncTask rawDataManagerAsyncTask;
    private ChartFragment chartFragment;

    private int[] colors;


    private int MIN_HEIGHT = 10;

    public RealTimeChartComponent(Activity context, ChartFragment chartFragment, LineChart chart, TMBluetooth bluetooth, InitChartData initChartData) {
        this.context = context;
        this.mChart = chart;
        this.chartFragment = chartFragment;
        this.mBluetooth = bluetooth;
        this.initChartData = initChartData;
    }

    /**
     * onCreate
     **/
    public void onCreate() {
        mChart.setData(new LineData());
        initChartSettings();
        initColors(R.color.chartBlue, R.color.chartGreen, R.color.chartBrown, R.color.chartRed);
    }

    private void initColors(int... resources) {
        this.colors = new int[resources.length];
        for (int i = 0; i < resources.length; i++) {
            this.colors[i] = ContextCompat.getColor(this.context, resources[i]);
        }
    }

    private void initChartSettings() {
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setDrawLabels(false);
        for (int index = 0; index < 4; index++) {
            dataSetEntries.add(null);
        }

        mChart.setDrawGridBackground(false);
        mChart.setDescription(new Description() {{
            setText("Pression (mBar)");
        }});
        mChart.getLegend().setEnabled(false);

        //Init
        if (this.initChartData != null && this.initChartData.hasGraphs()) {
            for (int index = 0; index < 4; index++) {
                LimitedEntryList entries = addNewSet(this.context.getString(R.string.cylinder, index + 1), index, initChartData.getDataSetEntries(index));
                dataSetEntries.set(index, entries);
            }
        }
    }

    private int findMostActiveDataSet() {
        int selectedIndex = 0;
        float selectedHeight = 0;
        int index = 0;
        for (LimitedEntryList entries : this.dataSetEntries) {
            if (entries == null) break;
            if (entries.getHeight() > selectedHeight) {
                selectedIndex = index;
                selectedHeight = entries.getHeight();
            }
            index++;
        }
        return selectedIndex;
    }

    /**
     * onResume()
     **/
    public void onResume(Boolean wasPlaying, boolean updateState) {
        mBluetooth.addObserver(this);
        update(wasPlaying, updateState);
    }

    public void update(Boolean wasPlaying, boolean updateState) {
        if (mBluetooth.getConnectedDevice() != null || mBluetooth.hasConnectedFile()) { //If there's a connected device
            if (wasPlaying == null || wasPlaying) {
                play(updateState);
            } else { // !wasPlaying
                pause(updateState);
            }
        } else {
            pause(updateState);
        }
    }


    private boolean calibrationReady = false;
    private boolean calibrationStarted = false;
    private boolean calibrationDone = false;
    private boolean calibration = false;

    private LimitedEntryList calibratedDataSet;
    private float triggerValue;
    private GraphDirection direction;

    private int lastTriggerIndex = -1;
    private long lastTriggerCycle = -1;
    private int[] triggerIndices = new int[5];
    private long[] triggerIndexCycles = new long[5];
    private int triggersCount = 0;

    private int MIN_INDEX_DISTANCE = 20;

    /**
     * @param entries One entry per dataset
     */
    public void addEntries(Entry... entries) {
        int size = entries.length;
        for (int i = 0; i < size; i++) {
            addEntry(i, entries[i]);
        }
        checkCalibration();
    }

    private void checkCalibration() {
        if (calibrationReady && !calibrationStarted) {
            calibrationStarted = true;
            this.calibratedDataSet = this.dataSetEntries.get(findMostActiveDataSet());
            this.triggerValue = calibratedDataSet.getMiddleValue();
            Log.d("Trigger value", "" + triggerValue);
            float lastAddedValue = calibratedDataSet.getLastAddedValue();
            if (lastAddedValue > triggerValue) {
                direction = GraphDirection.DOWN;
            } else {
                direction = GraphDirection.UP;
            }
        } else if (calibrationStarted && !calibrationDone) {
            float lastAddedValue = calibratedDataSet.getLastAddedValue();
            if (direction == GraphDirection.DOWN) { //Going down
                if (lastAddedValue < triggerValue) {
                    int triggerIndex = calibratedDataSet.getCurrentX();
                    long triggerCycle = calibratedDataSet.getCycles();


                    if (!isIgnoreValue(triggerIndex, triggerCycle)) {
                        triggerIndices[triggersCount] = triggerIndex;
                        triggerIndexCycles[triggersCount++] = triggerCycle;
                        this.lastTriggerIndex = triggerIndex;
                        this.lastTriggerCycle = triggerCycle;
                        Log.d("Found trigger", "at index " + triggerIndices[triggersCount - 1] + "(" + triggerIndexCycles[triggersCount - 1] + ")");
                        direction = GraphDirection.UP;
                        if (triggersCount == triggerIndices.length) {
                            calibrationDone = true;
                            Log.d("Calibration", "calibrationDone");
                        }
                    }
                }
            } else { //Going up
                if (lastAddedValue > triggersCount) {
                    int triggerIndex = calibratedDataSet.getCurrentX();
                    long triggerCycle = calibratedDataSet.getCycles();

                    if (!isIgnoreValue(triggerIndex, triggerCycle)) {
                        triggerIndices[triggersCount] = triggerIndex;
                        triggerIndexCycles[triggersCount++] = triggerCycle;
                        this.lastTriggerIndex = triggerIndex;
                        this.lastTriggerCycle = triggerCycle;
                        Log.d("Found trigger", "at index " + triggerIndices[triggersCount - 1] + "(" + triggerIndexCycles[triggersCount - 1] + ")");
                        direction = GraphDirection.DOWN;
                        if (triggersCount == triggerIndices.length) {
                            calibrationDone = true;
                            Log.d("Calibration", "calibrationDone");
                        }
                    }
                }
            }
        } else if (calibrationDone && !calibration) {
            calibration = true;
            long diffCycles = triggerIndexCycles[triggerIndexCycles.length - 1] - triggerIndexCycles[0] + 1;
            int period = (int) (triggerIndices[triggerIndices.length - 1] * diffCycles - triggerIndices[0]);
            Log.d("Period", "" + period);
            for(LimitedEntryList entries : this.dataSetEntries) {
                entries.setSize(period);
            }
            mChart.setVisibleXRangeMinimum(0);
            mChart.setVisibleXRangeMaximum(period);
        }
    }

    private boolean isIgnoreValue(int triggerIndex, long triggerCycle) {
        if (lastTriggerIndex == -1) return false;

        int cycleDifference = (int) (triggerCycle - lastTriggerCycle) + 1;

        int previousIndex = lastTriggerIndex * cycleDifference;
        int currentIndex = triggerIndex * cycleDifference;

        return currentIndex - previousIndex < MIN_INDEX_DISTANCE;
    }

    public void addEntry(int index, Entry value) {
        LineData data = mChart.getData();

        if (data != null) {
            LimitedEntryList entries = this.dataSetEntries.get(index);
            if (entries == null) {
                entries = addNewSet(this.context.getString(R.string.cylinder, index + 1), index, null);
                this.dataSetEntries.set(index, entries);
            }

            synchronized (entries) {
                data.addEntry(value, index);
            }

            if (!calibrationReady && entries.getCycles() > 1) {
                calibrationReady = true;
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

    private LimitedEntryList addNewSet(String title, int index, LimitedEntryList initEntries) {

        int color = this.colors[index % this.colors.length];

        LimitedEntryList entries = initEntries != null && initEntries.size() == NB_POINTS ? initEntries : new LimitedEntryList(NB_POINTS);

        LineDataSet dataSet = new LineDataSet(entries, title);
        dataSet.setColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(0);
        dataSet.setValueTextColor(color);
        dataSet.setCircleColor(color);

        mChart.getData().addDataSet(dataSet);
        return entries;
    }


    public void play(boolean updateState) {
        if (rawDataManagerAsyncTask != null) {
            rawDataManagerAsyncTask.stop();
            rawDataManagerAsyncTask = null;
        }

        rawDataManagerAsyncTask = new RawDataManagerAsyncTask(mBluetooth.getDataManager(), this);
        if (updateState) this.chartFragment.setPlaying(true);
        if (Build.VERSION.SDK_INT >= 11)
            rawDataManagerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            rawDataManagerAsyncTask.execute();
    }

    public void pause(boolean updateState) {
        if (rawDataManagerAsyncTask != null) {
            rawDataManagerAsyncTask.stop();
            if (updateState) this.chartFragment.setPlaying(false);
        }
        rawDataManagerAsyncTask = null;
    }


    @Override
    public void update(Observable observable, Object o) {
        update(null, true);
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

    public int getNbGraphs() {
        int size = 0;
        for (LimitedEntryList entries : dataSetEntries) {
            if (entries != null && !entries.isEmpty()) {
                size++;
            }
        }
        return size;
    }

    public int getGraphsSize() {
        if (getNbGraphs() > 0) {
            return dataSetEntries.get(0).size();
        }
        return 0;
    }

    public float[] getDataSetValues(int dataSetIndex) {
        LimitedEntryList entries = this.dataSetEntries.get(dataSetIndex);
        if (entries != null && entries.size() > 0) {
            int size = entries.size();
            float[] result = new float[size];
            for (int i = 0; i < size; i++) {
                result[i] = entries.get(i).getY();
            }
            return result;
        }
        return null;
    }

    public List<LimitedEntryList> getDataSetEntries() {
        return dataSetEntries;
    }
}
