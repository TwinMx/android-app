package fr.isen.twinmx.database;

import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.database.model.Moto;
import io.realm.annotations.RealmModule;

/**
 * Created by pierredfc.
 */

@RealmModule(classes = {Moto.class, Maintenance.class})
public class TMRealmModule { }
