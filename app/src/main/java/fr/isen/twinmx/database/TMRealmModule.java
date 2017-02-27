package fr.isen.twinmx.database;

import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.database.model.RealmDevice;
import fr.isen.twinmx.database.model.RealmFloat;
import fr.isen.twinmx.database.model.RealmGraph;
import io.realm.annotations.RealmModule;

/**
 * Created by pierredfc.
 */

@RealmModule(classes = {Moto.class, Maintenance.class, RealmDevice.class, RealmGraph.class, RealmFloat.class})
public class TMRealmModule { }
