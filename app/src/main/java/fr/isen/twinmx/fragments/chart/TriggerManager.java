package fr.isen.twinmx.fragments.chart;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import fr.isen.twinmx.fragments.LimitedEntryList;
import fr.isen.twinmx.listeners.OnChangeInputListener;
import fr.isen.twinmx.listeners.OnCycleListener;
import fr.isen.twinmx.listeners.OnPeriodListener;
import fr.isen.twinmx.listeners.OnTriggerListener;
import fr.isen.twinmx.model.GraphDirection;

/**
 * Created by Clement on 10/02/2017.
 */

public class TriggerManager implements OnChangeInputListener {

    private final List<LimitedEntryList> dataSetEntries;

    private boolean triggerable = false; //calibration ready
    private LimitedEntryList triggeredDataSet;

    private int nbTriggersSinceLastPeriod = 0;
    private int nbPointsSinceLastPeriod = 0;

    private List<OnTriggerListener> onTriggerListeners = new LinkedList<>();
    private List<OnPeriodListener> onPeriodListeners = new LinkedList<>();
    private List<OnCycleListener> onCycleListeners = new LinkedList<>();


    public TriggerManager(List<LimitedEntryList> dataSetEntries) {
        this.dataSetEntries = dataSetEntries;
    }

    public boolean isTriggerable() {
        return triggerable;
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


    public void onTrigger(long nbPointsSinceLastTrigger, GraphDirection direction, LimitedEntryList dataSet) {
        if (dataSet != triggeredDataSet) return;

        for (OnTriggerListener l : onTriggerListeners) {
            l.onTrigger(nbPointsSinceLastTrigger, direction);
        }

        if (nbPointsSinceLastTrigger > 0) {
            nbTriggersSinceLastPeriod++;
            nbPointsSinceLastPeriod += nbPointsSinceLastTrigger;
            if (nbTriggersSinceLastPeriod >= 2) {
                onPeriod();
                this.nbPointsSinceLastPeriod = 0;
                this.nbTriggersSinceLastPeriod = 0;
            }
        }

    }

    private void onPeriod() {
        long value = this.nbPointsSinceLastPeriod;
        for (OnPeriodListener l : onPeriodListeners) {
            l.onPeriod(value);
        }
    }

    public void onCycle(LimitedEntryList dataSet) {
        if (!triggerable) {
            setTriggerable(true);
            return;
        }
        if (dataSet == triggeredDataSet) {
            for(OnCycleListener l : onCycleListeners) {
                l.onCycle();
            }
        }
    }


    public void addOnTriggerListener(OnTriggerListener onTriggerListener) {
        if (!this.onTriggerListeners.contains(onTriggerListener)) {
            this.onTriggerListeners.add(onTriggerListener);
        }
    }

    public void addOnPeriodListener(OnPeriodListener onPeriodListener) {
        if (!this.onPeriodListeners.contains(onPeriodListener)) {
            this.onPeriodListeners.add(onPeriodListener);
        }
    }

    public void addOnCycleListener(OnCycleListener onCycleListener) {
        if (!this.onCycleListeners.contains(onCycleListener)) {
            this.onCycleListeners.add(onCycleListener);
        }
    }


    @Override
    public void onConnect() {
        this.triggerable = false;
        this.triggeredDataSet = null;

        nbTriggersSinceLastPeriod = 0;
        nbPointsSinceLastPeriod = 0;
    }
}
