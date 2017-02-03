package fr.isen.twinmx.model;

import android.os.Bundle;

import com.github.mikephil.charting.data.Entry;

import fr.isen.twinmx.fragments.LimitedEntryList;

/**
 * Created by Clement on 03/02/2017.
 */
public class InitChartData {


    private final float[][] graphs;

    public InitChartData(Bundle b, String stateNbGraphs, String stateGraphSize, String stateGraph) {
        this(b.containsKey(stateNbGraphs) ? b.getInt(stateNbGraphs) : 0, b.containsKey(stateGraphSize) ? b.getInt(stateGraphSize) : 0, b, stateGraph);
    }

    private static float[][] getFloatArrays(int nbGraphs, int graphSize, Bundle b, String stateGraph) {
        if (nbGraphs > 0 && graphSize > 0) {
            float[][] arrays = new float[nbGraphs][graphSize];
            for(int graph = 0; graph < nbGraphs; graph++) {
                float[] values = b.getFloatArray(stateGraph+graph);
                for(int i = 0; i < values.length; i++) {
                    arrays[graph][i] = values[i];
                }
            }
            return arrays;
        }
        return null;
    }

    public InitChartData(int nbGraphs, int graphSize, Bundle b, String stateGraph) {
        this(getFloatArrays(nbGraphs, graphSize, b, stateGraph));
    }

    public InitChartData(float[][] graphs) {
        this.graphs = graphs;
    }

    public boolean hasGraphs() {
        return graphs != null;
    }

    public float[][] getGraphs() {
        return graphs;
    }

    public LimitedEntryList getDataSetEntries(int dataSetIndex) {
        try {
            float[] values = graphs[dataSetIndex];
            LimitedEntryList entries = new LimitedEntryList(values.length);

            int i = 0;
            for(Entry entry : entries) {
                entry.setY(values[i++]);
            }
            return entries;
        } catch(Exception ex) {
            return null;
        }
    }

}
