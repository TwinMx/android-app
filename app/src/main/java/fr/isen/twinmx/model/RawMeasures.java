package fr.isen.twinmx.model;

/**
 * Created by Clement on 12/01/2017.
 */
public class RawMeasures {

    private int capacity;
    private int size;
    private int[] array;

    public RawMeasures(int capacity) {
        this.array = new int[capacity];
        this.capacity = capacity;
        this.size = 0;
    }

    public boolean add(double value) {
        return add((int)value);
    }

    public boolean add(int value) {
        if (size >= capacity) {
            return false;
        }
        array[size++] = value;
        return true;
    }

    public boolean isComplete() {
        return size >= capacity;
    }

    public void clear() {
        this.size = 0;
    }

    public int[] getValues() {
        if (isComplete()) {
            return array;
        }
        else {
            int[] values = new int[size];
            for(int i = 0; i < size; i++) {
                values[i] = array[i];
            }
            return values;
        }
    }
}
