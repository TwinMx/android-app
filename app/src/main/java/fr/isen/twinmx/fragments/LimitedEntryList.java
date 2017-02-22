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
    private boolean waitForTrigger = false;

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
            if (!waitForTrigger) {
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
                checkFindTrigger(entryY, this.triggerIndex);
            } else {
                this.incrementTriggerIndex();
                checkFindTrigger(entryY, this.triggerIndex);
            }
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
        this.onCycle();
    }

    public void resetTriggerIndex() {
        this.triggerIndex = 0;
    }

    private void incrementX() {
        this.currentX++;
        if (this.currentX >= size) {
            this.reset();
        }
        this.incrementTriggerIndex();
    }

    private void incrementTriggerIndex() {
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
            if (rangeToRemove > 0) {
                this.removeRange(0, rangeToRemove);
            } else if (-rangeToRemove > 0) {
                this.addRange(-rangeToRemove, 0);
            }
            int index = 0;
            for (Entry e : this) {
                e.setX(index++);
            }
            this.size = period;
            currentX = 0;
        }
    }

    private void addRange(int range, float defaultValue) {
        int currentSize = this.size();
        for (int i = 0; i < range; i++) {
            super.add(new Entry(currentSize + i - 1, defaultValue));
        }
    }

    public void setTrigger(float trigger) {
        this.trigger = trigger;
        this.updateDirection();
    }

    private void updateDirection() {
        this.direction = getDirectionForValue(getLastAddedValue());
    }

    private GraphDirection getDirectionForValue(float value) {
        if (trigger != NO_TRIGGER) {
            return value > trigger ? GraphDirection.GOING_DOWN : GraphDirection.GOING_UP;
        }
        return null;
    }

    private boolean isGoingUp() {
        return direction == GraphDirection.GOING_UP;
    }

    private boolean isGoingDown() {
        return direction == GraphDirection.GOING_DOWN;
    }

    private void checkFindTrigger(float value, long triggerIndex) {
        //float lastAddedValue = get(x).getY();
        if (isGoingDown() && value < trigger) {
            triggerFound(triggerIndex, this.direction);
            this.direction = GraphDirection.GOING_UP;
        } else if (isGoingUp() && value > trigger) {
            triggerFound(triggerIndex, this.direction);
            this.direction = GraphDirection.GOING_DOWN;
        }
    }

    private void triggerFound(long nbPointsSinceLastTrigger, GraphDirection direction) {
        if (isTriggerOk(nbPointsSinceLastTrigger)) {
            if (nbPointsSinceLastTrigger == NO_TRIGGER) {
                nbPointsSinceLastTrigger = 0;
            }
            onTrigger(nbPointsSinceLastTrigger, direction);
            resetTriggerIndex();
        }
    }

    private void onTrigger(long nbPointsSinceLastTrigger, GraphDirection direction) {
        if (this.triggerManager != null) {
            this.triggerManager.onTrigger(nbPointsSinceLastTrigger, direction, this);
        }
    }

    private void onCycle() {
        if (this.triggerManager != null) {
            this.triggerManager.onCycle(this);
        }
    }

    private boolean isTriggerOk(long nbPointsSinceLastTrigger) {
        if (nbPointsSinceLastTrigger == NO_TRIGGER) return true;
        return nbPointsSinceLastTrigger > MIN_TRIGGER_DISTANCE;
    }

    public float getTrigger() {
        return trigger;
    }

    public void setWaitForTrigger(boolean waitForTrigger) {
        this.waitForTrigger = waitForTrigger;
    }

    public int computePeriod() {

        synchronized (currentX) {
            int beginX = -1;
            int endX = -1;
            int triggerCount = 0;
            int index = 0;

            GraphDirection direction = getDirectionForValue(get(0).getY());

            for (Entry entry : this) {
                float value = entry.getY();
                if (GraphDirection.GOING_DOWN == direction && value < trigger) { //Trigger found
                    triggerCount++;
                    if (beginX == -1) {
                        beginX = index;
                    } else if (index % 2 == 0) {
                        endX = index;
                    }
                    direction = GraphDirection.GOING_UP;
                } else if (GraphDirection.GOING_UP == direction && value > trigger) { //Trigger found
                    triggerCount++;
                    if (beginX == -1) {
                        beginX = index;
                    } else if (index % 2 == 0) {
                        endX = index;
                    }
                    direction = GraphDirection.GOING_DOWN;
                }
                index++;
            }

            if (triggerCount % 2 == 1) { //Un trigger en trop
                triggerCount--;
            }

            triggerCount--;

            try {
                return (endX - beginX) / (triggerCount / 2);
            } catch (Exception ex) {
                return -1;
            }
        }


    }
}
