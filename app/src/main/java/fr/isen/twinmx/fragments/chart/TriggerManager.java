package fr.isen.twinmx.fragments.chart;

import java.util.LinkedList;
import java.util.List;

import fr.isen.twinmx.model.TMDataSet;
import fr.isen.twinmx.listeners.OnChangeInputListener;
import fr.isen.twinmx.listeners.OnCycleListener;
import fr.isen.twinmx.listeners.OnPeriodListener;
import fr.isen.twinmx.listeners.OnTriggerListener;
import fr.isen.twinmx.model.GraphDirection;
import fr.isen.twinmx.model.TMDataSets;

/**
 * Created by Clement on 10/02/2017.
 */

public class TriggerManager implements OnChangeInputListener {

    private final TMDataSets dataSets;

    private boolean triggerable = false; //calibration ready

    private int nbTriggersSinceLastPeriod = 0;
    private int nbPointsSinceLastPeriod = 0;

    private List<OnTriggerListener> onTriggerListeners = new LinkedList<>();
    private List<OnPeriodListener> onPeriodListeners = new LinkedList<>();
    private List<OnCycleListener> onCycleListeners = new LinkedList<>();
    private boolean disabled = false;

    public TriggerManager(TMDataSets dataSets) {
        this.dataSets = dataSets;
    }

    public boolean isTriggerable() {
        return triggerable;
    }

    public void setTriggerable(boolean value) {
        if (disabled) return;
        this.triggerable = value;
        this.updateTrigger();
    }

    public void updateTrigger() {
        TMDataSet dataSet = getTriggeredDataSet();
        if (dataSet != null) {
            dataSet.setTrigger(dataSet.getMiddleValue());
        }
    }

    public TMDataSet getTriggeredDataSet() {
        return this.dataSets.getCalibratedDataSet();
    }


    public void notifyTrigger(long nbPointsSinceLastTrigger, GraphDirection direction) {
        for (OnTriggerListener l : onTriggerListeners) {
            l.onTrigger(nbPointsSinceLastTrigger, direction);
        }

        if (nbPointsSinceLastTrigger > 0) {
            nbTriggersSinceLastPeriod++;
            nbPointsSinceLastPeriod += nbPointsSinceLastTrigger;
            if (nbTriggersSinceLastPeriod >= 2) {
                notifyPeriod();
                this.nbPointsSinceLastPeriod = 0;
                this.nbTriggersSinceLastPeriod = 0;
            }
        }

    }

    private void notifyPeriod() {
        long value = this.nbPointsSinceLastPeriod;
        for (OnPeriodListener l : onPeriodListeners) {
            l.onPeriod(value);
        }
    }

    public void notifyCycle() {
        if (!triggerable) {
            setTriggerable(true);
            return;
        }
        for (OnCycleListener l : onCycleListeners) {
            l.onCycle();
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
        reset();
    }

    @Override
    public void onDisconnect() {
        //Nothing to do
    }

    public void reset() {
        reset(false);
    }

    public void disable() {
        reset(true);
    }

    private void reset(boolean disabled) {
        this.triggerable = false;

        nbTriggersSinceLastPeriod = 0;
        nbPointsSinceLastPeriod = 0;

        this.disabled = disabled;
    }

    public void removeListeners() {
        this.onCycleListeners.clear();
        this.onTriggerListeners.clear();
        this.onPeriodListeners.clear();
    }


    public boolean isDisabled() {
        return disabled;
    }
}
