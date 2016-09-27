package fr.isen.twinmx.listeners;

import java.util.List;

import fr.isen.twinmx.model.History;

/**
 * Created by pierredfc.
 */

public interface RequestListener {
    void onResponseReceived(List<History> results);
}
