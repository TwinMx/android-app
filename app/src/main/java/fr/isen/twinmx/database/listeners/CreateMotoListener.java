package fr.isen.twinmx.database.listeners;

import fr.isen.twinmx.database.model.Moto;

/**
 * Created by pierredfc.
 */

public interface CreateMotoListener {

    void createMoto(Moto moto, OnCreateMotoCallback callback);


    interface OnCreateMotoCallback {
        void onSuccess();
        void onFailure();
    }
}
