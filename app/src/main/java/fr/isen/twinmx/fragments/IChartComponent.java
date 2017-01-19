package fr.isen.twinmx.fragments;

/**
 * Created by Clement on 19/01/2017.
 */

public interface IChartComponent {

    void setVisible(int index, boolean show);
    void fitScreen();
    void onCreate();
    void onResume();
    void update();
    void feedMultiple();
    void play();
    void pause();
}
