package fr.isen.twinmx.database;

import io.realm.Realm;

/**
 * Created by pierredfc.
 */

public class RealmConfiguration {

    private static Realm realm;

    public static void setRealm(Realm newRealm)
    {
        realm = newRealm;
    }

    public static Realm getRealm() {
        return realm;
    }
}
