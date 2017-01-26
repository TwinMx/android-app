package fr.isen.twinmx.model;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Clement on 20/01/2017.
 */

public class LimitedLinkedList<T> extends LinkedList<T> {

    final int capacity;

    public LimitedLinkedList(int capacity) {
        super();
        this.capacity = capacity;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        this.removeMultipleFirst(this.size() + c.size() - capacity);
        return super.addAll(c);
    }

    private void removeMultipleFirst(int nb) {
        if (nb <= 0) return;
        for (int i = 0; i < nb; i++) {
            this.removeFirst();
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        this.removeMultipleFirst(this.size() + c.size() - capacity);
        return super.addAll(index, c);
    }

    @Override
    public void add(int index, T element) {
        this.removeMultipleFirst(this.size() + 1 - capacity);
        super.add(index, element);
    }

    @Override
    public T removeFirst() {
        return super.removeFirst();
    }

    @Override
    public void addFirst(T t) {
        super.addFirst(t);
    }

    @Override
    public void addLast(T t) {
        this.removeMultipleFirst(this.size() + 1 - capacity);
        super.addLast(t);
    }

    @Override
    public T removeLast() {
        this.removeMultipleFirst(this.size() + 1 - capacity);
        return super.removeLast();
    }

    @Override
    public boolean add(T t) {
        this.removeMultipleFirst(this.size() + 1 - capacity);
        return super.add(t);
    }
}
