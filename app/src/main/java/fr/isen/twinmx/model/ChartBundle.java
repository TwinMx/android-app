package fr.isen.twinmx.model;

import android.os.Bundle;

import java.util.List;

/**
 * Created by Clement on 03/02/2017.
 */
public class ChartBundle {

    public static final String STATE_NB_DATASETS = "STATE_NB_DATASETS";
    public static final String STATE_DATASET = "STATE_DATASET_";
    public static final String STATE_NB_POINTS = "STATE_NB_POINTS";

    private final Bundle b;
/*    public static final String STATE_TRIGGER = "STATE_TRIGGER";*/



    public ChartBundle(Bundle b) {
        this.b = b;
    }

    public int getNbDataSets() {
        return b.containsKey(STATE_NB_DATASETS) ? b.getInt(STATE_NB_DATASETS) : 0;
    }

    public int getNbPoints() {
        return b.containsKey(STATE_NB_POINTS) ? b.getInt(STATE_NB_POINTS) : 0;
    }

    public float[] getDataSet(int index) {
        try {
            return b.containsKey(STATE_DATASET+index) ?
                    b.getFloatArray(STATE_DATASET+index)
                    : null;
        } catch(Exception ex) {
            return null;
        }
    }


    public static void putNbGraphs(Bundle outState, int nbGraphs) {
        outState.putInt(ChartBundle.STATE_NB_DATASETS, nbGraphs);
    }

    public static void putNbPoints(Bundle outState, int nbPoints) {
        outState.putInt(ChartBundle.STATE_NB_POINTS, nbPoints);
    }

    public static void putGraphs(Bundle outState, List<TMDataSet> dataSets) {
        for(int i = 0; i < dataSets.size(); i++) {
            putGraph(outState, i, toFloatArray(dataSets.get(i)));
        }
    }

    private static void putGraph(Bundle outState, int index, float[] values) {
        outState.putFloatArray(ChartBundle.STATE_DATASET + index, values);
    }

/*    public static void putTrigger(Bundle outState, TMDataSet calibratedDataSet) {
        try {
            outState.putFloat(ChartBundle.STATE_TRIGGER, calibratedDataSet.getTrigger());
        }
        catch(Exception ex) {
            // No trigger
        }
    }*/

    public static float[] toFloatArray(TMDataSet dataSet) {
        if (dataSet != null && dataSet.size() > 0) {
            int size = dataSet.size();
            float[] result = new float[size];
            for (int i = 0; i < size; i++) {
                result[i] = dataSet.get(i).getY();
            }
            return result;
        }
        return null;
    }
}
