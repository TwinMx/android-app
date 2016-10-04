package fr.isen.twinmx.database.listeners;

import fr.isen.twinmx.database.model.Moto;

/**
 * Created by pierredfc.
 */

public interface MotoListener {

    void createMoto(Moto moto, OnCreateMotoCallback callback);
    void deleteMoto(String name, OnDeleteMotoCallback callback);

    interface OnCreateMotoCallback
    {
        void onSuccess();
        void onFailure();
    }

    interface OnDeleteMotoCallback
    {
        void onSuccess();
        void onFailure();
    }
}
