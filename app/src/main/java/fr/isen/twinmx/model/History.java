package fr.isen.twinmx.model;

import java.util.Date;

/**
 * Created by pierredfc.
 */

public class History {

    private String name;
    private Date date;
    private String image;


    public History(String name, Date date)
    {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date.toString();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
