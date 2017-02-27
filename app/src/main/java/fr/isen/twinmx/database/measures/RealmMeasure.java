package fr.isen.twinmx.database.measures;

import fr.isen.twinmx.database.interfaces.AutoIncrement;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by pierredfc.
 */
public class RealmMeasure extends RealmObject implements AutoIncrement {

    public static final String DB_TYPE = "RealmMeasure";

    @PrimaryKey @Required
    private Long id;

    private int c0;
    private int c1;
    private int c2;
    private int c3;

    public RealmMeasure() {
    }

    public RealmMeasure(int c0, int c1, int c2, int c3) {
        this.c0 = c0;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
    }

    public RealmMeasure(RealmMeasure measure)
    {
        this(measure.getC0(), measure.getC1(), measure.getC2(), measure.getC3());
    }


    public int getC0() {
        return this.c0;
    }

    public void setC0(int c0) {
        this.c0 = c0;
    }

    public int getC1() {
        return this.c1;
    }

    public void setC1(int c1) {
        this.c1 = c1;
    }

    public int getC2() {
        return this.c2;
    }

    public void setC2(int c2) {
        this.c2 = c2;
    }

    public int getC3() {
        return this.c3;
    }

    public void setC3(int c3) {
        this.c3 = c3;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}