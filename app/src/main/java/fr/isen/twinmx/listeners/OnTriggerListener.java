package fr.isen.twinmx.listeners;


import fr.isen.twinmx.model.enums.GraphDirection;

/**
 * Created by Clement on 10/02/2017.
 */
public interface OnTriggerListener {

    void onTrigger(long nbPointsSinceLastTrigger, GraphDirection direction);

}
