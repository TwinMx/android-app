package fr.isen.twinmx.fragments.chart;


import com.github.mikephil.charting.charts.LineChart;

import fr.isen.twinmx.model.LimitedLinkedList;
import fr.isen.twinmx.model.TMDataSet;
import fr.isen.twinmx.listeners.OnChangeInputListener;
import fr.isen.twinmx.listeners.OnPeriodListener;

import fr.isen.twinmx.model.TMDataSets;


/**
 * Created by Clement on 10/02/2017.
 */

public class CalibrationManager implements OnPeriodListener, OnChangeInputListener {

    private static final int NB_PERIODS_DISPLAY = 2;

    private final LineChart mChart;
    private final TriggerManager triggerManager;
    private final TMDataSets dataSets;

    private boolean calibrated = false;
    private boolean disabled = false;
    private long nbPoints = TMDataSets.NB_POINTS;

    private LimitedLinkedList<Long> periods = new LimitedLinkedList<>(NB_PERIODS_DISPLAY);

    public CalibrationManager(LineChart chart, TMDataSets dataSets) {
        this.mChart = chart;
        this.triggerManager = dataSets.getTriggerManager();
        this.triggerManager.addOnPeriodListener(this);
        this.dataSets = dataSets;
    }

    @Override
    public void onPeriod(long nbPointsSinceLastPeriod) {
        periods.add(nbPointsSinceLastPeriod);

        if (disabled) return;

        if (!calibrated) {
            computeCalibration();
        }
    }

    private void computeCalibration() {
        if (periods.size() >= 2) {
            int period = (int) (periods.get(periods.size() - 1) + periods.get(periods.size() - 2));
            makeCalibration(period);
            return;
        }

        TMDataSet dataSet = this.triggerManager.getTriggeredDataSet();
        if (dataSet != null) {
            int period = dataSet.computePeriod();
            if (period > 0) {
                makeCalibration(period * NB_PERIODS_DISPLAY);
            }
        }
    }

    private void makeCalibration(int nbPoints) {
        calibrated = true;
        setSizes(nbPoints);
        this.setNbPoints(nbPoints);
    }

    private void setSizes(int nbPoints) {
        dataSets.setNbPoints(nbPoints);
    }

    public void reset() {
        reset(false);
    }

    private void reset(boolean disabled) {
        calibrated = false;
        setSizes(TMDataSets.NB_POINTS);
        mChart.getXAxis().setAxisMinimum(0);
        mChart.getXAxis().setAxisMaximum(TMDataSets.NB_POINTS);
        this.disabled = disabled;
    }

    @Override
    public void onConnect() {
        reset();
    }

    public void disable() {
        reset(true);
    }

    public long getNbPoints() {
        return nbPoints;
    }

    private boolean isComputing = false;

    public void recalibrate() {
        if (!isComputing) {
            isComputing = true;
            computeCalibration();
            isComputing = false;
        }
    }

    public boolean isCalibrated() {
        return calibrated;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setNbPoints(long nbPoints) {
        this.nbPoints = nbPoints;
        mChart.getXAxis().setAxisMinimum(0);
        mChart.getXAxis().setAxisMaximum(nbPoints);
        mChart.getAxisRight().setAxisMinimum(0);
    }

    public void setCalibrated(boolean calibrated) {
        this.calibrated = calibrated;
    }
}
