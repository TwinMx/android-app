package fr.isen.twinmx.utils;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by cdupl on 2/25/2017.
 */

public class SingleObservable extends Observable {
    @Override
    public synchronized void addObserver(Observer o) {
        if (this.countObservers() > 0) {
            this.deleteObservers();
        }
        super.addObserver(o);
    }
}
