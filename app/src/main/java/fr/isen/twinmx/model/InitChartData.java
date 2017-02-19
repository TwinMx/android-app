package fr.isen.twinmx.model;

import android.os.Bundle;

import com.github.mikephil.charting.data.Entry;

import fr.isen.twinmx.fragments.chart.TriggerManager;

/**
 * Created by Clement on 03/02/2017.
 */
public class InitChartData {


    private final float[][] graphs;
    private final float trigger;
    private final long calibrationWidth;

    public InitChartData(Bundle b, String stateNbGraphs, String stateGraphSize, String stateGraph, String stateTrigger, String stateCalibrationWidth) {
        this(
                b.containsKey(stateNbGraphs) ? b.getInt(stateNbGraphs) : 0,
                b.containsKey(stateGraphSize) ? b.getInt(stateGraphSize) : 0,
                b,
                stateGraph,
                b.containsKey(stateTrigger) ? b.getFloat(stateTrigger) : -1,
                b.containsKey(stateCalibrationWidth) ? b.getLong(stateCalibrationWidth) : -1

        );
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

    public InitChartData(int nbGraphs, int graphSize, Bundle b, String stateGraph, float trigger, long calibrationWidth) {
        this(getFloatArrays(nbGraphs, graphSize, b, stateGraph), trigger, calibrationWidth);
    }

    public InitChartData(float[][] graphs, float trigger, long calibrationWidth) {
        this.graphs = graphs;
        this.trigger = trigger;
        this.calibrationWidth = calibrationWidth;
    }

    public boolean hasGraphs() {
        return graphs != null;
    }

    public float[][] getGraphs() {
        return graphs;
    }

    public TMDataSet getDataSetEntries(int dataSetIndex, TMDataSets dataSets) {
        try {
            float[] values = graphs[dataSetIndex];
            TMDataSet entries = new TMDataSet(values.length, dataSets);

            int i = 0;
            for(Entry entry : entries) {
                entry.setY(values[i++]);
            }
            entries.setTrigger(trigger);
            return entries;
        } catch(Exception ex) {
            return null;
        }
    }

    public long getCalibrationWidth() {
        return calibrationWidth;
    }
}
