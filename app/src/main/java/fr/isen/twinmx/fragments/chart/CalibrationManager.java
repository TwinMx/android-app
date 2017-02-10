package fr.isen.twinmx.fragments.chart;

import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import fr.isen.twinmx.fragments.LimitedEntryList;
import fr.isen.twinmx.listeners.OnTriggerListener;

/**
 * Created by Clement on 10/02/2017.
 */

public class CalibrationManager implements OnTriggerListener {


    private final List<LimitedEntryList> dataSetEntries;
    private final LineChart mChart;
    private final TriggerManager triggerManager;

    private boolean triggersFound = false;
    private boolean calibration = false;

    private int triggersCount = 0;
    private long twoPeriods = 0;


    public CalibrationManager(LineChart chart, TriggerManager triggerManager, List<LimitedEntryList> dataSetEntries) {
        this.mChart = chart;
        this.triggerManager = triggerManager;
        this.triggerManager.addOnTriggerListener(this);
        this.dataSetEntries = dataSetEntries;
    }

    @Override
    public void onTrigger(long nbPointsSinceLastTrigger) {
        if (!triggersFound) {
            this.twoPeriods += nbPointsSinceLastTrigger;
            if (++this.triggersCount >= 5) {
                triggersFound = true;
            }
        } else if (!calibration) {
            calibration = true;
            for(LimitedEntryList entries : this.dataSetEntries) {
                entries.setSize((int) twoPeriods);
            }
            mChart.setVisibleXRangeMinimum(0);
            mChart.setVisibleXRangeMaximum(twoPeriods);
        }
    }
}
