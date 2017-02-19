package fr.isen.twinmx.model;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import fr.isen.twinmx.R;
import fr.isen.twinmx.fragments.chart.CalibrationManager;
import fr.isen.twinmx.fragments.chart.TriggerManager;
import fr.isen.twinmx.listeners.OnChangeInputListener;
import fr.isen.twinmx.listeners.OnCycleListener;
import fr.isen.twinmx.listeners.OnTriggerListener;

/**
 * Created by Clement on 19/02/2017.
 */

public class TMDataSets implements OnChangeInputListener, OnCycleListener, OnTriggerListener {

    public static int NB_POINTS = 200;

    public static final String STATE_NB_GRAPHS = "STATE_NB_GRAPHS";
    public static final String STATE_GRAPH = "STATE_GRAPH_";
    public static final String STATE_GRAPH_SIZE = "STATE_GRAPH_SIZE";
    public static final String STATE_TRIGGER = "STATE_TRIGGER";
    public static final String STATE_CALIBRATION_WIDTH = "STATE_CALIBRATION_WIDTH";

    private final int nbGraphs;
    private final int defaultNbPoints;
    private int nbPoints;
    private final List<TMDataSet> dataSets;

    private int calibratedIndex = -1;
    private TMDataSet calibratedDataSet = null;


    private final Activity activity;
    private final LineChart chart;

    private int[] colors;

    private int x = 0;

    private TriggerManager triggerManager;
    private CalibrationManager calibrationManager;

    public TMDataSets(Activity activity, LineChart chart, int nbGraphs, int defaultNbPoints) {
        this.activity = activity;
        this.nbGraphs = nbGraphs;
        this.defaultNbPoints = defaultNbPoints;
        this.nbPoints = defaultNbPoints;
        this.chart = chart;

        initColors(R.color.chartBlue, R.color.chartGreen, R.color.chartBrown, R.color.chartRed);

        this.triggerManager = new TriggerManager(this);
        this.calibrationManager = new CalibrationManager(chart, this);
        this.triggerManager.addOnCycleListener(this);
        this.triggerManager.addOnTriggerListener(this);

        this.dataSets = initDataSets();

    }

    private void initColors(int... resources) {
        this.colors = new int[resources.length];
        for (int i = 0; i < resources.length; i++) {
            this.colors[i] = ContextCompat.getColor(this.activity, resources[i]);
        }
    }

    private List<TMDataSet> initDataSets() {

        chart.setData(new LineData());

        List<TMDataSet> dataSets = new ArrayList<>(nbGraphs);
        for (int i = 0; i < nbGraphs; i++) {
            TMDataSet dataSet = addNewSet(this.activity.getString(R.string.cylinder, i + 1), i, null);
            dataSets.add(dataSet);
        }
        return dataSets;
    }

    public int getNbGraphs() {
        int size = 0;
        for (TMDataSet entries : dataSets) {
            if (entries != null && !entries.isEmpty()) {
                size++;
            }
        }
        return size;
    }

    public int getNbPoints() {
        return this.nbPoints;
    }

    public void setNbPoints(Integer nbPoints) {
        if (nbPoints == null) {
            nbPoints = defaultNbPoints;
        }

        this.nbPoints = nbPoints;

        for (TMDataSet dataSet : dataSets) {
            dataSet.setSize(nbPoints);
        }

        if (x >= nbPoints) {
            x = 0;
        }
    }

    @Override
    public void onConnect() {
        this.triggerManager.reset();
        this.calibrationManager.reset();
    }

    private void setWaitForTrigger(boolean value) {
        if (this.calibratedDataSet != null) {
            this.calibratedDataSet.setWaitForTrigger(value);
        }
    }

    private boolean isWaitForTrigger() {
        return this.calibratedDataSet != null ? this.calibratedDataSet.isWaitForTrigger() : false;
    }

    @Override
    public void onCycle() {
        setWaitForTrigger(true);
    }

    public void notifyCycle() {
        this.triggerManager.notifyCycle();
    }

    public void notifyTrigger(long nbPointsSinceLastTrigger, GraphDirection direction) {
        this.triggerManager.notifyTrigger(nbPointsSinceLastTrigger, direction);
    }

    @Override
    public void onTrigger(long nbPointsSinceLastTrigger, GraphDirection direction) {
        if (isWaitForTrigger()) {
            if (direction == GraphDirection.GOING_UP) {
                setWaitForTrigger(false);
            }
        }
    }

    public void addEntries(Entry... entries) {
        if (!isWaitForTrigger()) {
            int size = entries.length;
            for (int i = 0; i < size; i++) {
                addEntry(i, entries[i]);
            }
            incrementX();
        }
        else { //in case we wait for a trigger (before starting again the graph at index 0, we don't add a new point but check if a trigger was reached, and increment the triggerIndex (for period computation)
            if (this.calibratedDataSet != null) {
                this.calibratedDataSet.checkTrigger(entries[calibratedIndex].getY());
            }
        }

    }

    public void addEntry(int index, Entry entry) {
        LineData data = chart.getData();

        if (data != null) {
            TMDataSet entries = this.dataSets.get(index);
            synchronized (entries) {
                data.addEntry(entry, index);
            }

            data.notifyDataChanged();
        }
    }

    public void refreshChart() {
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private TMDataSet addNewSet(String title, int index, TMDataSet initEntries) {

        int color = this.colors[index % this.colors.length];

        TMDataSet entries = initEntries != null && initEntries.size() == nbPoints ? initEntries : new TMDataSet(nbPoints, this);

        LineDataSet dataSet = new LineDataSet(entries, title);
        dataSet.setColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(0);
        dataSet.setValueTextColor(color);
        dataSet.setCircleColor(color);

        chart.getData().addDataSet(dataSet);
        return entries;
    }

    public TMDataSet getCalibratedDataSet() {
        if (calibratedDataSet == null) {
            findMostActiveDataSet();
        }
        return calibratedDataSet;
    }

    private void findMostActiveDataSet() {
        float selectedHeight = 0;
        calibratedDataSet = null;
        calibratedIndex = -1;

        int index = 0;
        for (TMDataSet entries : this.dataSets) {
            if (entries == null) continue;
            if (entries.getHeight() > selectedHeight) {
                selectedHeight = entries.getHeight();
                calibratedDataSet = entries;
                calibratedIndex = index;
            }
            index++;
        }
    }


    public void recalibrate() {
        this.calibrationManager.recalibrate();
    }

    public void disableCalibration() {
        this.calibrationManager.disable();
        this.triggerManager.disable();
    }

    public void enableCalibration() {
        this.calibrationManager.reset();
        this.triggerManager.reset();
    }

    public float[] toFloatArray(int datasetIndex) {
        TMDataSet entries = this.dataSets.get(datasetIndex);
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


    public AcquisitionSaveRequest createAcquisitionSaveRequest() {
        List<TMDataSet> entries = this.dataSets;
        if (entries != null && entries.size() > 0 && entries.get(0) != null && entries.get(0).size() > 0) {
            return new AcquisitionSaveRequest(entries);
        }
        return null;
    }

    public TriggerManager getTriggerManager() {
        return triggerManager;
    }

    public void save(Bundle outState) {
        int nbGraphs = this.getNbGraphs();
        outState.putInt(STATE_NB_GRAPHS, nbGraphs);
        outState.putInt(STATE_GRAPH_SIZE, this.getNbPoints());
        for (int i = 0; i < nbGraphs; i++) {
            outState.putFloatArray(STATE_GRAPH + i, this.toFloatArray(i));
        }
        try {
            outState.putFloat(STATE_TRIGGER, this.calibratedDataSet.getTrigger());
        } catch(Exception ex) {
            //
        }

        try {
            outState.putLong(STATE_CALIBRATION_WIDTH, this.calibrationManager.getNbPoints());
        } catch(Exception ex) {
            //
        }
    }

    public void load(InitChartData initChartData) {

        if (initChartData != null && initChartData.hasGraphs()) {

            chart.setData(new LineData());

            for (int index = 0; index < 4; index++) {
                TMDataSet entries = addNewSet(this.activity.getString(R.string.cylinder, index + 1), index, initChartData.getDataSetEntries(index, this));
                dataSets.set(index, entries);
            }
        }

        if (initChartData != null && initChartData.getCalibrationWidth() != -1) {
            this.calibrationManager.setNbPoints(initChartData.getCalibrationWidth());
        }
    }

    public int getX() {
        return x;
    }

    public void incrementX() {
        x++;
        if (this.x >= nbPoints) {
            resetX();
        }
    }

    private void resetX() {
        this.x = 0;
        this.notifyCycle();
    }


    public void removeListeners() {
        this.triggerManager.removeListeners();
    }
}
