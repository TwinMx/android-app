package fr.isen.twinmx.fragments.chart;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;

import java.util.Observable;
import java.util.Observer;

import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.async.RawDataManagerAsyncTask;
import fr.isen.twinmx.fragments.ChartFragment;
import fr.isen.twinmx.listeners.OnChangeInputListener;
import fr.isen.twinmx.model.AcquisitionSaveRequest;
import fr.isen.twinmx.model.ChartBundle;
import fr.isen.twinmx.model.TMDataSets;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

/**
 * Created by Clement on 19/01/2017.
 */


public class TMChart implements Observer, OnChangeInputListener {

    private final Activity activity;
    private final LineChart mChart;
    private final ChartBundle chartBundle;
    private TMDataSets dataSets;
    private TMBluetooth mBluetooth;
    private RawDataManagerAsyncTask rawDataManagerAsyncTask;
    private ChartFragment chartFragment;


    public TMChart(Activity activity, ChartFragment chartFragment, LineChart chart, TMBluetooth bluetooth, ChartBundle chartBundle) {
        this.activity = activity;
        this.mChart = chart;
        this.chartFragment = chartFragment;
        this.mBluetooth = bluetooth;
        this.chartBundle = chartBundle;
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
            setText(TMApplication.getContext().getString(R.string.pressure));
        }});
        mChart.getLegend().setEnabled(false);

        dataSets.load(chartBundle);
        mChart.getAxisRight().setAxisMinimum(0);

        this.mBluetooth.addOnChangeInputListener(this.dataSets);
        this.mBluetooth.addOnChangeInputListener(this);
    }


    /**
     * onResume()
     **/
    public void onResume(Boolean wasPlaying) {
        mBluetooth.addObserver(this);
        update(wasPlaying != null ? wasPlaying : true);
    }

    public void update(boolean wasPlaying) {
        if (mBluetooth.getConnectedDevice() != null || mBluetooth.hasConnectedFile()) { //If there's a connected device
            if (wasPlaying) {
                play();
            } else { // !wasPlaying
                pause(false);
            }
        } else {
            pause(false);
        }
    }

    public void play() {
        if (rawDataManagerAsyncTask != null) {
            rawDataManagerAsyncTask.stopAndWait();
            rawDataManagerAsyncTask = null;
        }

        rawDataManagerAsyncTask = new RawDataManagerAsyncTask(mBluetooth.getDataManager(), this.dataSets);
        this.chartFragment.updatePlayingState(true);
        if (Build.VERSION.SDK_INT >= 11)
            rawDataManagerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            rawDataManagerAsyncTask.execute();
    }

    public void pause(boolean wait) {
        if (rawDataManagerAsyncTask != null) {
            if (wait) {
                rawDataManagerAsyncTask.stopAndWait();
            } else {
                rawDataManagerAsyncTask.stop();
            }
            this.chartFragment.updatePlayingState(false);
        }
        rawDataManagerAsyncTask = null;
    }


    @Override
    public void update(Observable observable, Object o) {
        update(true);
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

    public void disableCalibration() {
        this.dataSets.disableCalibration();
    }

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

    @Override
    public void onConnect() {
        //Nothing to do
    }

    @Override
    public void onDisconnect() {
        this.pause(false);
    }
}
