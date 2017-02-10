package fr.isen.twinmx.listeners;

import fr.isen.twinmx.fragments.chart.TriggerManager;

/**
 * Created by Clement on 10/02/2017.
 */
public interface OnTriggerListener {

    void onTrigger(long nbPointsSinceLastTrigger);

}
