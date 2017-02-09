package fr.isen.twinmx.fragments;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clement on 26/01/2017.
 */
public class LimitedEntryList extends ArrayList<Entry> {

    private Integer currentX = 0;
    private int size = 0;

    private boolean isMinMaxInit = true;
    float yMax = 0;
    float yMin = 0;

    private long cycles = 0;

    public LimitedEntryList(int size) {
        super(size);
        this.size = size;
        for(int i = 0; i < size; i++) {
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
            }
            else {
                yMax = entryY;
                yMin = entryY;
                isMinMaxInit = false;
            }
            incrementX();
        }
    }

    public float getHeight() {
        return yMax-yMin;
    }

    public float getMiddleValue() {
        return yMin + getHeight()/2;
    }

    public void reset() {
        this.currentX = 0;
        this.cycles++;
    }

    private void incrementX() {
        this.currentX++;
        if (this.currentX >= size) {
            this.reset();
        }
    }

    public long getCycles() {
        return cycles;
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
            this.clear();
            this.size = period;
            for(int i = 0; i < size; i++) {
                super.add(new Entry(i, 0));
            }
            currentX = 0;
            cycles = 0;
        }
    }
}
