package fr.isen.twinmx.fragments.chart;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import fr.isen.twinmx.fragments.ChartFragment;
import fr.isen.twinmx.fragments.LimitedEntryList;
import fr.isen.twinmx.listeners.OnChangeInputListener;
import fr.isen.twinmx.listeners.OnPeriodListener;
import fr.isen.twinmx.listeners.OnTriggerListener;
import fr.isen.twinmx.model.GraphDirection;

/**
 * Created by Clement on 10/02/2017.
 */

public class CalibrationManager implements OnPeriodListener, OnChangeInputListener {

    private static final int NB_PERIODS_DISPLAY = 2;

    private final List<LimitedEntryList> dataSetEntries;
    private final LineChart mChart;
    private final TriggerManager triggerManager;

    private boolean calibrated = false;
    private boolean disabled = false;
    private ChartFragment chartFragment;
    private long nbPoints = RealTimeChartComponent.NB_POINTS;


    public CalibrationManager(LineChart chart, TriggerManager triggerManager, List<LimitedEntryList> dataSetEntries, ChartFragment chartFragment) {
        this.mChart = chart;
        this.triggerManager = triggerManager;
        this.triggerManager.addOnPeriodListener(this);
        this.dataSetEntries = dataSetEntries;
        this.chartFragment = chartFragment;
    }

    @Override
    public void onPeriod(long nbPointsSinceLastPeriod) {
        if (disabled) return;

        if (!calibrated) {
            computeCalibration();
        }
    }

    public void setNbPoints(long twoPeriods) {
        if (twoPeriods > 0) return;
        makeCalibration((int) twoPeriods);
    }

    private void computeCalibration() {
        LimitedEntryList dataSet = this.triggerManager.getTriggeredDataSet();
        if (dataSet != null) {
            int period = dataSet.computePeriod();
            if (period > 0) {
                makeCalibration(period * NB_PERIODS_DISPLAY);
            }
        }
    }

    private void makeCalibration(int nbPoints) {
        calibrated = true;
        this.nbPoints = nbPoints;
        setSizes(nbPoints);
        mChart.getXAxis().setAxisMinimum(0);
        mChart.getXAxis().setAxisMaximum(nbPoints);
        this.chartFragment.setCalibrating(false);
    }

    private void setSizes(int nbPoints) {
        for (LimitedEntryList entries : this.dataSetEntries) {
            if (entries != null) {
                entries.setSize(nbPoints);
            }
        }
    }

    public void reset() {
        reset(false);
    }

    private void reset(boolean disabled) {
        calibrated = false;
        setSizes(RealTimeChartComponent.NB_POINTS);
        mChart.getXAxis().setAxisMinimum(0);
        mChart.getXAxis().setAxisMaximum(RealTimeChartComponent.NB_POINTS);
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

    public void recalibrate() {
        computeCalibration();
    }
}
