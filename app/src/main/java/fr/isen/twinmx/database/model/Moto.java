package fr.isen.twinmx.database.model;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * Created by pierredfc.
 */
public class Moto extends RealmObject implements Serializable {

    private String name;
    private RealmList<Maintenance> maintenances;
    private String date;

    public Moto() {
        this.maintenances = new RealmList<>();
    }

    public Moto(String name, String date)
    {
        this();
        this.name = name;
        this.date = date;
    }

    public Moto(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Maintenance> getMaintenances() {return this.maintenances;}

    public void setMaintenances(RealmList<Maintenance> maintenances) {this.maintenances = maintenances;}

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
