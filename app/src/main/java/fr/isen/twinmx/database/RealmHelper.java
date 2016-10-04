package fr.isen.twinmx.database;

import android.util.Log;

import fr.isen.twinmx.database.listeners.MotoListener;
import fr.isen.twinmx.database.model.Moto;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by pierredfc.
 */

public class RealmHelper implements MotoListener {

    private static Realm realm;

    public static void setRealm(Realm newRealm)
    {
        realm = newRealm;
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
        realm.beginTransaction();

        RealmResults<Moto> result = realm.where(Moto.class).equalTo("name", name).findAll();
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

    public void getMotos()
    {
        realm.beginTransaction();

        RealmResults<Moto> result = realm.where(Moto.class)
                .findAll();

        for (Moto m : result)
        {
            Log.d("GETMOTOS", m.getName());
        }
    }

    /**void createKanjiCompOrUpdateFrequency() {
     // This query is fast because "character" is an indexed field
     KanjiComp kanjoComp = realm.where(KanjiComp.class)
     .equalTo("character", someValue)
     .findFirst();
     realm.beginTransaction();
     if (kanjiComp == null) {
     KanjiComp kanjiComp = realm.createObject(KanjiComp.class);
     // set the fields here
     } else {
     kanjiComp.setFrequency(kanjiComp.getFrequency() + 1);
     }
     realm.commitTransaction();
     }*/
}
