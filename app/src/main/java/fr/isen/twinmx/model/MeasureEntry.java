package fr.isen.twinmx.model;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by Clement on 19/01/2017.
 */

public class MeasureEntry {
    private int lineIndex;
    private Entry value;

    public MeasureEntry(int lineIndex, Entry value) {
        this.lineIndex = lineIndex;
        this.value = value;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public Entry getValue() {
        return value;
    }

    public void setValue(Entry value) {
        this.value = value;
    }
}
