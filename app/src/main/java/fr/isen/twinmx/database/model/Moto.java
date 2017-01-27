package fr.isen.twinmx.database.model;

import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import fr.isen.twinmx.database.interfaces.AutoIncrement;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by pierredfc.
 */
public class Moto extends RealmObject implements AutoIncrement {

    public static final String DB_TYPE = "Moto";

    public static int INDEX = 0;

    @PrimaryKey @Required
    private Long id = null;

    @Required
    private String name;
    private String date;
    private String image;

    private RealmList<Maintenance> maintenances;

    public Moto() {
        this.maintenances = new RealmList<>();
    }

    public Moto(String name, String photo)
    {
        this(name, DateFormat.getDateTimeInstance().format(new Date()), photo);
    }

    public Moto(String name) {
        this(name, DateFormat.getDateTimeInstance().format(new Date()), null);
    }

    public Moto(String name, String date, String photo)
    {
        this();
        this.name = name;
        this.date = date;
        this.image = photo;
    }

    public Moto(Moto moto)
    {
        this.id = moto.getId();
        this.name = moto.getName();
        this.date = moto.getDate();
        this.image = moto.getImage();
        this.maintenances = moto.getMaintenances();
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
