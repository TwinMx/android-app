package fr.isen.twinmx.fragments;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import fr.isen.twinmx.fragments.chart.TriggerManager;
import fr.isen.twinmx.model.GraphDirection;

/**
 * Created by Clement on 26/01/2017.
 */
public class LimitedEntryList extends ArrayList<Entry> {

    private final TriggerManager triggerManager;
    private Integer currentX = 0;
    private int size = 0;

    private boolean isMinMaxInit = true;
    float yMax = 0;
    float yMin = 0;

    public static final int NO_TRIGGER = -1;
    private float trigger = NO_TRIGGER;
    private GraphDirection direction = null;
    private long triggerIndex = NO_TRIGGER;

    private static int MIN_TRIGGER_DISTANCE = 10;

    public LimitedEntryList(int size, TriggerManager triggerManager) {
        super(size);
        this.size = size;
        this.triggerManager = triggerManager;
        for (int i = 0; i < size; i++) {
            super.add(new Entry(i, 0));
        }
    }

    @Override
    public boolean add(Entry entry) {
        add(entry.getY());
        return true;
    }

    public void add(float entryY) {
        synchronized (currentX) {
            get(currentX).setY(entryY);
            if (!isMinMaxInit) {
                if (entryY > yMax) yMax = entryY;
                if (entryY < yMin) yMin = entryY;
            } else {
                yMax = entryY;
                yMin = entryY;
                isMinMaxInit = false;
            }

            incrementX();
            checkFindTrigger(getCurrentX(), this.triggerIndex);
        }
    }

    public float getHeight() {
        return yMax - yMin;
    }

    public float getMiddleValue() {
        return yMin + getHeight() / 2;
    }

    public void reset() {
        this.currentX = 0;
        if (this.triggerManager != null) {
            this.triggerManager.addCycle();
        }
    }

    public void resetTriggerIndex() {
        this.triggerIndex = 0;
    }

    private void incrementX() {
        this.currentX++;
        if (this.currentX >= size) {
            this.reset();
        }
        if (triggerIndex >= 0) {
            triggerIndex++;
        }
    }

    public List<Entry> toList() {
        return this;
    }

    public float getLastAddedValue() {
        return currentX - 1 >= 0 ? get(currentX - 1).getY() : get(size - 1).getY();
    }

    public Integer getCurrentX() {
        return currentX;
    }

    public void setSize(int period) {
        synchronized (currentX) {
            int rangeToRemove = this.size - period;
            this.removeRange(0, rangeToRemove);
            int index = 0;
            for(Entry e : this) {
                e.setX(index++);
            }
            this.size = period;
            currentX = 0;
        }
    }

    public void setTrigger(float trigger) {
        this.trigger = trigger;
        this.updateDirection();
    }

    private void updateDirection() {
        if (trigger != NO_TRIGGER) {
            if (getLastAddedValue() > trigger) {
                direction = GraphDirection.GOING_DOWN;
            } else {
                direction = GraphDirection.GOING_UP;
            }
        }
    }

    private boolean isGoingUp() {
        return direction == GraphDirection.GOING_UP;
    }

    private boolean isGoingDown() {
        return direction == GraphDirection.GOING_DOWN;
    }

    private void checkFindTrigger(int x, long triggerIndex) {
        float lastAddedValue = get(x).getY();
        if (isGoingDown() && lastAddedValue < trigger) {
            triggerFound(triggerIndex);
            this.direction = GraphDirection.GOING_UP;
        } else if (isGoingUp() && lastAddedValue > trigger) {
            triggerFound(triggerIndex);
            this.direction = GraphDirection.GOING_DOWN;
        }
    }

    private void triggerFound(long nbPointsSinceLastTrigger) {
        if (isTriggerOk(nbPointsSinceLastTrigger)) {
            if (nbPointsSinceLastTrigger == NO_TRIGGER) {
                nbPointsSinceLastTrigger = 0;
            }
            if (nbPointsSinceLastTrigger > 80) {
                Log.d("too", "high");
            }
            onTrigger(nbPointsSinceLastTrigger);
            resetTriggerIndex();
        }
    }

    private void onTrigger(long nbPointsSinceLastTrigger) {
        if (this.triggerManager != null) {
            this.triggerManager.onTrigger(nbPointsSinceLastTrigger, this);
        }
    }

    private boolean isTriggerOk(long nbPointsSinceLastTrigger) {
        if (nbPointsSinceLastTrigger == NO_TRIGGER) return true;
        return nbPointsSinceLastTrigger > MIN_TRIGGER_DISTANCE;
    }

    public float getTrigger() {
        return trigger;
    }
}
