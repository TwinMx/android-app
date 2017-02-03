package fr.isen.twinmx.database.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Clement on 27/01/2017.
 */

public class RealmFloat extends RealmObject {

    @Required
    private Float value;

    public RealmFloat() {

    }

    public RealmFloat(float value) {
        this.value = value;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
