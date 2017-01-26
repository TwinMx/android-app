package fr.isen.twinmx.model;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by Clement on 12/01/2017.
 */
public class RawMeasures {

    private final int capacity;
    private int size;
    private final int[] array;
    private int nbMaxPoints = 200;

    public RawMeasures(int capacity) {
        this.array = new int[capacity];
        this.capacity = capacity;
        this.size = 0;
    }

    public boolean add(double value) {
        return add((int)value);
    }

    /**
     *
     * @param value
     * @return true if completed
     */
    public boolean add(int value) {
        if (size >= capacity) {
            //throw new IndexOutOfBoundsException("RawMeasures array is full");
            clear();
        }
        array[size++] = value;
        return isComplete();
    }

    public boolean isComplete() {
        return size == capacity;
    }


    public void clear() {
        this.size = 0;
    }

    public Entry[] toEntries(int x) {
        Entry[] entries = new Entry[capacity];
        for (int i = 0; i < capacity; i++) {
            entries[i] = new Entry(x, array[i]);
        }
        return entries;
    }
}
