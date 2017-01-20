package fr.isen.twinmx.database.model;

import java.util.ArrayList;

import fr.isen.twinmx.database.interfaces.AutoIncrement;
import fr.isen.twinmx.database.measures.RealmMeasure;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by pierredfc.
 */
public class Maintenance extends RealmObject implements AutoIncrement {

    public static final String DB_TYPE = "Maintenance";

    @PrimaryKey @Required
    private Long id = null;
    private String date;
    private String note;
    private RealmList<RealmMeasure> measures;

    public Maintenance() {}

    public Maintenance(String  date, String note)
    {
        this.date = date;
        this.note = note;
        this.measures = new RealmList<>();
    }

    public Maintenance(String date, String note, ArrayList<RealmMeasure> measuresList)
    {
        this(date, note);

        for (RealmMeasure measure : measuresList)
        {
            this.measures.add(new RealmMeasure(measure));
        }
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public RealmList<RealmMeasure> getMeasures() {
        return this.measures;
    }

    public void setMeasures(RealmList<RealmMeasure> measures) {
        this.measures = measures;
    }

    @Override
    public Long getId() { return this.id;}

    @Override
    public void setId(Long id) { this.id = id;}
}
