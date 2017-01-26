package fr.isen.twinmx.model;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * Created by Clement on 20/01/2017.
 */

public class EntriesLimitedLinkedList extends LimitedLinkedList<Entry> {

    Comparator<? super Entry> comparator = new EntryXComparator();

    private Object lock = new Object();

    public EntriesLimitedLinkedList(int capacity, Entry... items) {
        super(capacity, items);
    }

    private void sortByX() {
        Collections.sort(this, comparator);
    }

    @Override
    public void addFirst(Entry entry) {
        super.addFirst(entry);
        sortByX();
    }

    @Override
    public void addLast(Entry entry) {
        super.addLast(entry);
        sortByX();
    }

    //Used by MPAndroidChart
    @Override
    public boolean add(Entry entry) {
        synchronized (lock) {
            int x = this.size();
            entry.setX(x);
            boolean result = super.add(entry);
            sortByX();
            return result;
        }

    }

    @Override
    public void add(int index, Entry element) {
        Log.d("add", "index");
        super.add(index, element);
        sortByX();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Entry> c) {
        Log.d("addAll", "index");
        boolean result = super.addAll(index, c);
        sortByX();
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends Entry> c) {
        Log.d("addAll", "entries");
        return super.addAll(c);
    }

    @Override
    public Entry get(int index) {
        synchronized (lock) {
            Entry t = super.get(index);
            if (t != null) {
                t.setX(index);
                Log.d("get", "" + t.getX());
            }
            return t;
        }
    }
}
