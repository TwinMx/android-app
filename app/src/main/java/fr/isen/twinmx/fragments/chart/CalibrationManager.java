package fr.isen.twinmx.fragments.chart;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import fr.isen.twinmx.fragments.LimitedEntryList;
import fr.isen.twinmx.listeners.OnChangeInputListener;
import fr.isen.twinmx.listeners.OnTriggerListener;
import fr.isen.twinmx.model.GraphDirection;

/**
 * Created by Clement on 10/02/2017.
 */

public class CalibrationManager implements OnTriggerListener, OnChangeInputListener {


    private final List<LimitedEntryList> dataSetEntries;
    private final LineChart mChart;
    private final TriggerManager triggerManager;

    private boolean triggersFound = false;
    private boolean calibrated = false;

    private int triggersCount = 0;
    private long twoPeriods = 0;

    private boolean disabled = false;


    public CalibrationManager(LineChart chart, TriggerManager triggerManager, List<LimitedEntryList> dataSetEntries) {
        this.mChart = chart;
        this.triggerManager = triggerManager;
        this.triggerManager.addOnTriggerListener(this);
        this.dataSetEntries = dataSetEntries;
    }

    @Override
    public void onTrigger(long nbPointsSinceLastTrigger, GraphDirection direction) {
        if (disabled) return;

        if (!triggersFound) {
            this.twoPeriods += nbPointsSinceLastTrigger;
            if (++this.triggersCount >= 5) {
                triggersFound = true;
            }
        }
        if (triggersFound && !calibrated) {
            makeCalibration((int) this.twoPeriods);
        }
    }

    public long getNbPoints() {
        return twoPeriods;
    }

    public void setNbPoints(long twoPeriods) {
        if (twoPeriods > 0) return;
        this.twoPeriods = twoPeriods;
        makeCalibration((int) twoPeriods);
    }

    private void makeCalibration(int nbPoints) {
        triggersFound = true;
        calibrated = true;
        setSizes(nbPoints);
        mChart.getXAxis().setAxisMinimum(0);
        mChart.getXAxis().setAxisMaximum(twoPeriods);
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
        triggersFound = false;
        calibrated = false;
        setSizes(RealTimeChartComponent.NB_POINTS);
        mChart.getXAxis().setAxisMinimum(0);
        mChart.getXAxis().setAxisMaximum(RealTimeChartComponent.NB_POINTS);
        triggersCount = 0;
        twoPeriods = 0;

        this.disabled = disabled;
    }

    @Override
    public void onConnect() {
        reset();
    }

    public void disable() {
        reset(true);
    }
}
