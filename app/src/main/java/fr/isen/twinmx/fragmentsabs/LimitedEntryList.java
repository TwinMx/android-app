package fr.isen.twinmx.fragmentsabs;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clement on 26/01/2017.
 */
public class LimitedEntryList extends ArrayList<Entry> {

    private Integer currentX = 0;
    private int size = 0;

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
            incrementX();
        }
    }

    public void reset() {
        this.currentX = 0;
    }

    private void incrementX() {
        this.currentX = this.currentX + 1 < size ? this.currentX + 1 : 0;
    }

    public List<Entry> toList() {
        return this;
    }
}
