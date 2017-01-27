package fr.isen.twinmx.database;

import android.util.Log;

import java.util.List;

import fr.isen.twinmx.database.listeners.MaintenanceListener;
import fr.isen.twinmx.database.listeners.MotoListener;
import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.database.model.Moto;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by pierredfc.
 */

public class RealmHelper implements MotoListener, MaintenanceListener {

    private static final String TAG = "RealmHelper";

    private static Realm realm;

    public static void setRealm(Realm newRealm)
    {
        realm = newRealm;
    }

    public static Realm getRealm() {
        return realm;
    }

    @Override
    public void createMoto(Moto moto, OnCreateMotoCallback callback) {
        realm.beginTransaction();

        Moto m = realm.createObject(Moto.class);
        m.setName(moto.getName());
        m.setDate(moto.getDate());
        m.setImage(moto.getImage());
        realm.commitTransaction();

        if (callback != null)
        {
            callback.onSuccess();
        }
    }

    @Override
    public void deleteMoto(String name, OnDeleteMotoCallback callback) {
        //realm.beginTransaction();

        RealmResults<Moto> result = realm.where(Moto.class).equalTo("name", name).findAll();
        final boolean isSuccess = result.deleteAllFromRealm();

        //realm.commitTransaction();

        if (callback != null)
        {
            if (isSuccess)
            {
                callback.onSuccess();
            }
            else
            {
                callback.onFailure();
            }
        }
    }

    public List<Moto> getMotos()
    {
        realm.beginTransaction();

        RealmResults<Moto> results = realm.where(Moto.class).findAll();

        for (Moto m : results)
        {
            Log.d(TAG, m.getName());
        }

        return results;
    }

    public void getMaintenances()
    {
        realm.beginTransaction();

        RealmResults<Maintenance> results = realm.where(Maintenance.class).findAll();

        for (Maintenance m : results)
        {
            Log.d(TAG, m.getNote());
        }
    }

    @Override
    public void addMaintenance(Maintenance maintenance, onAddMaintenanceCallback callback)
    {
        realm.beginTransaction();

        Maintenance m = realm.createObject(Maintenance.class);
        m.setDate(maintenance.getDate());
        m.setId(maintenance.getId());
        m.setNote(maintenance.getNote());
        m.setMeasures(maintenance.getMeasures());
        realm.commitTransaction();

        if (callback != null)
        {
            callback.onSuccess();
        }
    }

    @Override
    public void removeMaintenance(int maintenanceId, onRemoveMaintenanceCallback callback)
    {
        realm.beginTransaction();

        RealmResults<Maintenance> result = realm.where(Maintenance.class).equalTo("id", maintenanceId).findAll();
        final boolean isSuccess = result.deleteAllFromRealm();

        realm.commitTransaction();

        if (callback != null)
        {
            if (isSuccess)
            {
                callback.onSuccess();
            }
            else
            {
                callback.onFailure();
            }
        }
    }
}
