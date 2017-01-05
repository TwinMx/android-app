package fr.isen.twinmx.database.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by pierredfc.
 */
public class Moto extends RealmObject {

    @PrimaryKey
    private String name;
    @Required
    private String date;
    private String image;
    private RealmList<Maintenance> maintenances;

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

    public String getImage() { return this.image; }

    public void setImage(String image) { this.image = image;}
}
