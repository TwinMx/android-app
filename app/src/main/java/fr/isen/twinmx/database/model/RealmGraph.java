package fr.isen.twinmx.database.model;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by Clement on 27/01/2017.
 */
@RealmClass
public class RealmGraph implements RealmModel {

    private RealmList<RealmFloat> measures;

    public RealmGraph() {
        this.measures = new RealmList<>();
    }

    public RealmGraph(List<Entry> entries) {
        this.measures = new RealmList<>();
        for(Entry entry : entries) {
            this.measures.add(new RealmFloat(entry.getY()));
        }
    }

    public RealmList<RealmFloat> getMeasures() {
        return measures;
    }

    public void setMeasures(RealmList<RealmFloat> measures) {
        this.measures = measures;
    }
}
