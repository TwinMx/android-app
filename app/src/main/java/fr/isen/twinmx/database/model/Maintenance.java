package fr.isen.twinmx.database.model;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

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
    private RealmList<RealmGraph> graphs;

    public Maintenance() {}

    public Maintenance(String  date, String note, List<List<Entry>> graphs)
    {
        this.date = date;
        this.note = note;
        this.graphs = new RealmList<>();
        for(List<Entry> graph : graphs) {
            this.graphs.add(new RealmGraph(graph));
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

    public RealmList<RealmGraph> getGraphs() {
        return graphs;
    }

    public void setGraphs(RealmList<RealmGraph> graphs) {
        this.graphs = graphs;
    }

    @Override
    public Long getId() { return this.id;}

    @Override
    public void setId(Long id) { this.id = id;}
}
