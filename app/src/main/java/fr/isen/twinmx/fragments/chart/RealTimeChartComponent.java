package fr.isen.twinmx.fragments.chart;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import fr.isen.twinmx.async.RawDataManagerAsyncTask;
import fr.isen.twinmx.fragments.ChartFragment;
import fr.isen.twinmx.model.AcquisitionSaveRequest;
import fr.isen.twinmx.model.TMDataSet;
import fr.isen.twinmx.model.InitChartData;
import fr.isen.twinmx.model.TMDataSets;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

/**
 * Created by Clement on 19/01/2017.
 */

public class RealTimeChartComponent implements Observer {

    private final Activity activity;
    private final LineChart mChart;
    private final InitChartData initChartData;
    private TMDataSets dataSets;
    private TMBluetooth mBluetooth;
    private RawDataManagerAsyncTask rawDataManagerAsyncTask;
    private ChartFragment chartFragment;

    public RealTimeChartComponent(Activity activity, ChartFragment chartFragment, LineChart chart, TMBluetooth bluetooth, InitChartData initChartData) {
        this.activity = activity;
        this.mChart = chart;
        this.chartFragment = chartFragment;
        this.mBluetooth = bluetooth;
        this.initChartData = initChartData;
    }

    /**
     * onCreate
     **/
    public void onCreate() {
        this.dataSets = new TMDataSets(activity, mChart, 4, TMDataSets.NB_POINTS);
        initChartSettings();
    }



    private void initChartSettings() {
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setDrawLabels(false);

        mChart.setDrawGridBackground(false);
        mChart.setDescription(new Description() {{
            setText("Pression (mBar)");
        }});
        mChart.getLegend().setEnabled(false);
        mChart.getAxisRight().setAxisMinimum(0);

        dataSets.load(initChartData);

        this.mBluetooth.addOnChangeInputListener(this.dataSets);
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
                pause(false, updateState);
            }
        } else {
            pause(false, updateState);
        }
    }

    public void play(boolean updateState) {
        if (rawDataManagerAsyncTask != null) {
            rawDataManagerAsyncTask.stopAndWait();
            rawDataManagerAsyncTask = null;
        }

        rawDataManagerAsyncTask = new RawDataManagerAsyncTask(mBluetooth.getDataManager(), this.dataSets);
        if (updateState) this.chartFragment.setPlaying(true);
        if (Build.VERSION.SDK_INT >= 11)
            rawDataManagerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            rawDataManagerAsyncTask.execute();
    }

    public void pause(boolean wait, boolean updateState) {
        if (rawDataManagerAsyncTask != null) {
            if (wait) {
                rawDataManagerAsyncTask.stopAndWait();
            } else {
                rawDataManagerAsyncTask.stop();
            }
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
        this.dataSets.refreshChart();
    }

    public void fitScreen() {
        mChart.fitScreen();
    }

    public void resetCalibration() {
        this.dataSets.recalibrate();
    }

    public void disableCalibration() { this.dataSets.disableCalibration(); }

    public void enableCalibration() {
        this.dataSets.enableCalibration();
    }

    public AcquisitionSaveRequest createAcquisitionSaveRequest() {
        return this.dataSets.createAcquisitionSaveRequest();
    }

    public TriggerManager getTriggerManager() {
        return dataSets.getTriggerManager();
    }

    public void save(Bundle outState) {
        this.dataSets.save(outState);
    }

    public void onStop() {
        this.dataSets.removeListeners();
    }
}
