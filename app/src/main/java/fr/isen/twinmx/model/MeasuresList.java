package fr.isen.twinmx.model;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Clement on 19/01/2017.
 */

public class MeasuresList {

    private int refreshRate = 10;

    private final int nbLines;
    private final int maxNbMeasures;
    private int x = 0;
    private int[] currentMeasures;
    private int currentCount = 0;
    private int count = 5;

    private List<LinkedList<Entry>> data;

    public MeasuresList(int maxNbMeasures, int nbLines) {
        this.data = new ArrayList<LinkedList<Entry>>(nbLines);
        for (int i = 0; i < nbLines; i++) {
            this.data.add(new LinkedList<Entry>() {{
                add(new Entry(x, 0));
            }});
        }
        x++;
        this.currentMeasures = new int[nbLines];
        this.maxNbMeasures = maxNbMeasures;
        this.nbLines = nbLines;
    }

    public List<Entry> getLineEntries(int i) {
        return data.get(i);
    }

    public int size() {
        return data.get(0).size();
    }

    public boolean add(int[] items) {
        if (items != null && items.length == this.nbLines) {
            addToAverage(items);
            return true;
        }
        return false;
    }

    private void addToAverage(int[] items) {
        for (int i = 0; i < 4; i++) {
            this.currentMeasures[i] += items[i];
        }
        this.currentCount++;
        this.addToList();
    }

    private void addToList() {
        if (currentCount == this.count) {
            for (int i = 0; i < 4; i++) {
                add(i, x, this.currentMeasures[i] / this.count);
                this.currentMeasures[i] = 0;
            }
            x++;
            this.currentCount = 0;
        }
    }

    private void add(int lineIndex, int x, int y) {
        LinkedList<Entry> data = this.data.get(lineIndex);
        data.addLast(new Entry(x, y));
        while (data.size() > this.maxNbMeasures) {
            try {
                data.removeFirst();
            } catch (NoSuchElementException ex) {
                //
            }
        }
    }

    public Entry popFirst(int lineIndex) {
        return data.get(lineIndex).pop();
    }

    public Entry tryPopFirst(int lineIndex) {
        try {

            LinkedList<Entry> line = data.get(lineIndex);
            if (!line.isEmpty()) {
                return line.pop();
            }
        } catch (NoSuchElementException ex) {
            //
        }
        return null;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public void clear() {
        currentCount = 0;

    }
}
