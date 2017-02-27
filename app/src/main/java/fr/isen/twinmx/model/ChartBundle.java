package fr.isen.twinmx.model;

import android.os.Bundle;

import java.util.LinkedList;
import java.util.List;

import fr.isen.twinmx.fragments.chart.CalibrationManager;
import fr.isen.twinmx.fragments.chart.TriggerManager;

/**
 * Created by Clement on 03/02/2017.
 */
public class ChartBundle {

    /**** SAVE ****/

    //TriggerManager
    private static final String TRIGGER_MGR_DISABLED = "trigger-manager-disabled";

    //CalibrationManager
    private static final String CALIBRATION_MGR_CALIBRATED = "calibration-manager-calibrated";
    private static final String CALIBRATION_MGR_DISABLED = "calibration-manager-disabled";
    private static final String CALIBRATION_MGR_NB_POINTS = "calibration-manager-nb-points";

    //TMDataSets
    private static final String TM_DATASETS_NB_GRAPHS = "tm-datasets-nb-graphs";
    private static final String TM_DATASETS_NB_POINTS = "tm-datasets-nb-points";
    private static final String TM_DATASETS_GRAPH_ = "tm-datasets-graph-";

    private static String TM_DATASETS_GRAPH(int index) {
        return TM_DATASETS_GRAPH_ + index;
    }

    private final Bundle b;
/*    public static final String STATE_TRIGGER = "STATE_TRIGGER";*/

    public static void save(Bundle b, TMDataSets tm, TriggerManager tr, CalibrationManager cm) {
        save(b, tr);
        save(b, cm);
        save(b, tm);
    }

    private static void save(Bundle b, TriggerManager tr) {
        b.putBoolean(TRIGGER_MGR_DISABLED, tr.isDisabled());
    }

    private static void save(Bundle b, CalibrationManager cm) {
        b.putBoolean(CALIBRATION_MGR_CALIBRATED, cm.isCalibrated());
        b.putBoolean(CALIBRATION_MGR_DISABLED, cm.isDisabled());
        b.putLong(CALIBRATION_MGR_NB_POINTS, cm.getNbPoints());
    }

    private static void save(Bundle b, TMDataSets tm) {
        int nbGraphs = tm.getNbGraphs();
        b.putInt(TM_DATASETS_NB_GRAPHS, nbGraphs);
        b.putInt(TM_DATASETS_NB_POINTS, tm.getNbPoints());
        for (int i = 0; i < nbGraphs; i++) {
            b.putFloatArray(TM_DATASETS_GRAPH(i), toFloatArray(tm.getDataset(i)));
        }
    }

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


    /**** LOAD ****/

    public ChartBundle(Bundle b) {
        this.b = b;
    }

    public boolean isTriggerManagerDisabled() {
        return b.getBoolean(TRIGGER_MGR_DISABLED);
    }

    public boolean isCalibrationManagerCalibrated() {
        return b.getBoolean(CALIBRATION_MGR_CALIBRATED);
    }

    public boolean isCalibrationManagerDisabled() {
        return b.getBoolean(CALIBRATION_MGR_DISABLED);
    }

    public long getCalibrationManagerNbPoints() {
        return b.getLong(CALIBRATION_MGR_NB_POINTS);
    }

    public int getTMDataSetsNbGraphs() {
        return b.getInt(TM_DATASETS_NB_GRAPHS);
    }

    public int getTMDataSetsNbPoints() {
        return b.getInt(TM_DATASETS_NB_POINTS);
    }

    public List<TMDataSet> getTMDataSetsValues(TMDataSets tmDataSets) {
        int nbGraphs = getTMDataSetsNbGraphs();
        int nbPoints = getTMDataSetsNbPoints();
        List<TMDataSet> dataSets = new LinkedList<>();
        for(int i = 0; i < nbGraphs; i++) {
            float[] floats = getFloatArray(i);
            dataSets.add(new TMDataSet(floats, nbPoints, tmDataSets));
        }
        return dataSets;
    }

    private float[] getFloatArray(int index) {
        return b.getFloatArray(TM_DATASETS_GRAPH(index));
    }


}
