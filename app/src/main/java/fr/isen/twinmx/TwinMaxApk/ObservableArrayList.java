package fr.isen.twinmx.TwinMaxApk;

import java.util.ArrayList;
import java.util.Collection;

import fr.isen.twinmx.fragments.TwinMaxApkChartComponent;

/**
 * Created by Clement on 25/01/2017.
 */

public class ObservableArrayList<T> extends ArrayList<T> {

    private final TwinMaxApkChartComponent chartComponent;

    public ObservableArrayList(TwinMaxApkChartComponent chartComponent) {
        this.chartComponent = chartComponent;
    }

    public ObservableArrayList(TwinMaxApkChartComponent chartComponent, int capacity) {
        super(capacity);
        this.chartComponent = chartComponent;
    }

    @Override
    public boolean add(T object) {
        super.add(object);
        notifyAdd(size() - 1, 1);
        return true;
    }

    @Override
    public void add(int index, T object) {
        super.add(index, object);
        notifyAdd(index, 1);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        int oldSize = size();
        boolean added = super.addAll(collection);
        if (added) {
            notifyAdd(oldSize, size() - oldSize);
        }
        return added;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection) {
        boolean added = super.addAll(index, collection);
        if (added) {
            notifyAdd(index, collection.size());
        }
        return added;
    }

    @Override
    public void clear() {
        int oldSize = size();
        super.clear();
        if (oldSize != 0) {
            notifyRemove(0, oldSize);
        }
    }

    @Override
    public T remove(int index) {
        T val = super.remove(index);
        notifyRemove(index, 1);
        return val;
    }

    @Override
    public boolean remove(Object object) {
        int index = indexOf(object);
        if (index >= 0) {
            remove(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public T set(int index, T object) {
        T val = super.set(index, object);
        this.chartComponent.notifyChanged(this, index, 1);
        return val;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        notifyRemove(fromIndex, toIndex - fromIndex);
    }

    private void notifyAdd(int start, int count) {
        this.chartComponent.notifyInserted(this, start, count);
    }

    private void notifyRemove(int start, int count) {
        this.chartComponent.notifyRemoved(this, start, count);
    }

}
