package fr.isen.twinmx.fragments.chart;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import fr.isen.twinmx.fragments.LimitedEntryList;
import fr.isen.twinmx.listeners.OnTriggerListener;

/**
 * Created by Clement on 10/02/2017.
 */

public class TriggerManager {

    private final List<LimitedEntryList> dataSetEntries;

    private boolean triggerable = false; //calibration ready
    private LimitedEntryList triggeredDataSet;

    private long previous_nbPointsSinceLastTrigger = -1;
    private long current_nbPointsSinceLastTrigger = -1;
    int nbCycles = 0;

    private List<OnTriggerListener> onTriggerListeners = new LinkedList<>();


    public TriggerManager(List<LimitedEntryList> dataSetEntries) {
        this.dataSetEntries = dataSetEntries;
    }

    public void addCycle() {
        if (nbCycles < 1) {
            nbCycles++;
        }
    }

    public boolean isTriggerable() { return triggerable; }

    public void checkTriggerable() {
        if (!triggerable && nbCycles > 0) {
            setTriggerable(true);
        }
    }

    public void setTriggerable(boolean value) {
        this.triggerable = value;
        this.triggeredDataSet = findMostActiveDataSet();
        this.triggeredDataSet.setTrigger(this.triggeredDataSet.getMiddleValue());
    }

    public LimitedEntryList getTriggeredDataSet() {
        return this.triggeredDataSet;
    }

    private LimitedEntryList findMostActiveDataSet() {
        float selectedHeight = 0;
        LimitedEntryList selectedDataSet = null;
        for (LimitedEntryList entries : this.dataSetEntries) {
            if (entries == null) continue;
            if (entries.getHeight() > selectedHeight) {
                selectedHeight = entries.getHeight();
                selectedDataSet = entries;
            }
        }
        return selectedDataSet;
    }


    public void onTrigger(long nbPointsSinceLastTrigger, LimitedEntryList dataSet) {
        if (dataSet != triggeredDataSet) return;

        Log.d("trigger", ""+nbPointsSinceLastTrigger);

        this.previous_nbPointsSinceLastTrigger = current_nbPointsSinceLastTrigger;
        this.current_nbPointsSinceLastTrigger = nbPointsSinceLastTrigger;

        for(OnTriggerListener l : onTriggerListeners) {
            l.onTrigger(current_nbPointsSinceLastTrigger);
        }
    }


    public void addOnTriggerListener(OnTriggerListener onTriggerListener) {
        this.onTriggerListeners.add(onTriggerListener);
    }
}
