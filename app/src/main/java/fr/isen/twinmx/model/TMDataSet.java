package fr.isen.twinmx.model;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.isen.twinmx.database.model.RealmFloat;
import fr.isen.twinmx.database.model.RealmGraph;

/**
 * Created by Clement on 26/01/2017.
 */
public class TMDataSet extends ArrayList<Entry> {

    private final TMDataSets dataSets;
    private boolean notifyTriggers;
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

    public static final Object lock = new Object();

    public TMDataSet(int size, TMDataSets dataSets) {
        super(size);
        this.size = size;
        this.dataSets = dataSets;
        for (int i = 0; i < size; i++) {
            super.add(new Entry(i, 0));
        }
        this.notifyTriggers = false;
    }

    public TMDataSet(RealmGraph graph, TMDataSets dataSets) {
        super(graph.getMeasures().size());
        this.size = graph.getMeasures().size();
        this.dataSets = dataSets;
        int i = 0;
        for(RealmFloat value : graph.getMeasures()) {
            super.add(new Entry(i++, value.getValue()));
        }
        this.notifyTriggers = false;
    }

    @Override
    public boolean add(Entry entry) {
        add(entry.getY());
        return true;
    }

    public int getX() {
        return this.dataSets.getX();
    }

    public void add(float entryY) {
        int x = getX();

        get(x).setY(entryY);
        if (!isMinMaxInit) {
            if (entryY > yMax) yMax = entryY;
            if (entryY < yMin) yMin = entryY;
        } else {
            yMax = entryY;
            yMin = entryY;
            isMinMaxInit = false;
        }
        checkTrigger(entryY);
    }

    public boolean checkTrigger(float entryY) {
        incrementTriggerIndex();
        return checkFindTrigger(entryY, this.triggerIndex);
    }

    public float getHeight() {
        return yMax - yMin;
    }

    public float getMiddleValue() {
        return yMin + getHeight() / 2;
    }

    public void resetTriggerIndex() {
        this.triggerIndex = 0;
    }

    private void incrementX() {
        this.incrementTriggerIndex();
    }

    private void incrementTriggerIndex() {
        if (triggerIndex >= 0) {
            triggerIndex++;
        }
    }

    public List<Entry> toList() {
        List<Entry> entries = new ArrayList<>(this.size());
        int x = 0;
        for(Entry entry : this) {
            entries.add(new Entry(x++, entry.getY()));
        }
        return entries;
    }

    public int getPreviousX() {
        int x = getX() - 1;
        return x >= 0 ? x : size - 1;
    }

/*    public float getLastAddedValue() {
        int x = getX() - 1;
        return x >= 0 ? get(x).getY() : get(size - 1).getY();
    }*/

    public void setSize(int period) {
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
    }

    private void addRange(int range, float defaultValue) {
        int currentSize = this.size();
        for (int i = 0; i < range; i++) {
            super.add(new Entry(currentSize + i - 1, defaultValue));
        }
    }

    public void setTrigger(float trigger) {
        this.trigger = trigger;
        this.notifyTriggers = true;
        this.updateDirection(getPreviousX());
    }

    private void updateDirection(int x) {
        this.direction = getDirectionForValue(get(x).getY());
    }

    private GraphDirection getDirectionForValue(float value) {
        if (trigger != NO_TRIGGER) {
            return value > trigger ? GraphDirection.GOING_DOWN : GraphDirection.GOING_UP;
        }
        return null;
    }

    private boolean isGoingUp(GraphDirection direction) {
        return direction == GraphDirection.GOING_UP;
    }

    private boolean isGoingDown(GraphDirection direction) {
        return direction == GraphDirection.GOING_DOWN;
    }

    private boolean checkFindTrigger(float value, long triggerIndex) {
        synchronized (lock) {
            GraphDirection direction = this.direction;
            if (direction == null) {
                updateDirection(getPreviousX());
                direction = this.direction;
            }

            if (isGoingDown(direction) && value <= trigger) {
                this.direction = GraphDirection.GOING_UP;
                triggerFound(triggerIndex, direction);
                return true;
            } else if (isGoingUp(direction) && value >= trigger) {
                this.direction = GraphDirection.GOING_DOWN;
                triggerFound(triggerIndex, direction);
                return true;
            }

            return false;
        }
    }

    private void triggerFound(long nbPointsSinceLastTrigger, GraphDirection direction) {
        if (isTriggerOk(nbPointsSinceLastTrigger)) {
            if (nbPointsSinceLastTrigger == NO_TRIGGER) {
                nbPointsSinceLastTrigger = 0;
            }
            notifyTrigger(nbPointsSinceLastTrigger, direction);
            resetTriggerIndex();
        }
    }

    private GraphDirection previousGraphDirection = null;

    private void notifyTrigger(long nbPointsSinceLastTrigger, GraphDirection direction) {
        if (previousGraphDirection != direction) {
            if (notifyTriggers) {
                this.dataSets.notifyTrigger(nbPointsSinceLastTrigger, direction);
            }
        }
        else {
            if (notifyTriggers) {
                nbPointsSinceLastTrigger /= 2;
                //GraphDirection opposite = direction == GraphDirection.GOING_UP ? GraphDirection.GOING_DOWN : GraphDirection.GOING_UP;
                this.dataSets.notifyTrigger(nbPointsSinceLastTrigger, direction);
                this.dataSets.notifyTrigger(nbPointsSinceLastTrigger, direction);
                this.previousGraphDirection = null;
            }
        }
        previousGraphDirection = direction;

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

    public boolean isWaitForTrigger() {
        return waitForTrigger;
    }
}
