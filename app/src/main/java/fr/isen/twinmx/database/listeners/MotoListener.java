package fr.isen.twinmx.database.listeners;

/**
 * Created by pierredfc.
 */

public interface MotoListener {

    interface OnCreateMotoCallback
    {
        void onSuccess();
        void onFailure();
    }
}
