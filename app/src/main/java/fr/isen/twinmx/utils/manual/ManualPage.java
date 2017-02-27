package fr.isen.twinmx.utils.manual;

/**
 * Created by pierredfc.
 */

public class ManualPage {
    protected String category;
    protected String picture;
    protected String text;

    public ManualPage (String categorie, String picture, String text){
        this.text = text;
        this.picture = picture;
        this.category =categorie;
    }

    public String getText(){
        return text;
    }

    public String getPicture() {return picture;}

    public String getCategory() {return category;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("[").append(this.category).append("] ").append(this.text).toString();
    }
}
