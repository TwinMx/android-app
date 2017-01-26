package fr.isen.twinmx.fragments;

import android.app.Activity;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import fr.isen.twinmx.TwinMaxApk.ObservableArrayList;
import fr.isen.twinmx.TwinMaxApk.TwinMaxApkDataContainer;
import fr.isen.twinmx.TwinMaxApk.TwinMaxApkRawContainer;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothDataManager;

/**
 * Created by Clement on 25/01/2017.
 */

public class TwinMaxApkChartComponent {

    public static final int nbrPoints = 200;

    private final Activity context;
    private final LineChart mChart;
    private final TMBluetoothDataManager dataManager;

    public TwinMaxApkChartComponent(Activity context, LineChart chart, TMBluetoothDataManager dataManager) {
        this.context = context;
        this.mChart = chart;
        this.dataManager = dataManager;
    }

    private TwinMaxApkDataContainer mCleanData;
    private List<List<Entry>> entryLists;
    private List<ILineDataSet> lines;
    private TwinMaxApkRawContainer mRawContainer;

    public void onCreate() {
        mCleanData = new TwinMaxApkDataContainer(this);
        entryLists = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            entryLists.add(new ArrayList<Entry>(1) {{
                add(new Entry(0, 0));
            }});
        }
        lines = new ArrayList<ILineDataSet>(4) {{
            int i = 0;
            for (List<Entry> entryList : entryLists) {
                add(new LineDataSet(entryList, "data" + i++));
            }
        }};
        mChart.setData(new LineData(lines));
        settings();
    }

    private void settings() {
        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDescription(new Description() {{
            setText("Pression (mBar)");
        }});
        mChart.setTouchEnabled(true);

        int nbLines = lines.size();
        LineData lineData = mChart.getLineData();
        for (int i = 0; i < nbLines; i++) {
            LineDataSet lineDataSet = (LineDataSet) lineData.getDataSetByIndex(i);
            lineDataSet.setValueTextSize(0);
            lineDataSet.setDrawCircles(false);
            switch (i) {
                case 0:
                    lineDataSet.setColor(Color.rgb(237, 127, 16));
                    break;
                case 1:
                    lineDataSet.setColor(Color.rgb(58, 142, 186));
                    break;
                case 2:
                    lineDataSet.setColor(Color.rgb(127, 221, 76));
                    break;
                default:
                    lineDataSet.setColor(Color.rgb(247, 35, 12));
                    break;
            }
        }
    }


    private int mOscillationTrigger = 0;
    private final int DELTA_TRIGGER = 50;


    public void onResume() {

    }


    public <T> void notifyChanged(ObservableArrayList list, int start, int count) {
        //
    }


    private int changeCounter = 0;
    private boolean isStop = false;
    private boolean mustRefresh = true;

    public <T> void notifyInserted(ObservableArrayList ts, int start, int count) {
        if (isStop) {
            return;
        }

        changeCounter++;

        if (mCleanData.getSizeOfList() >= 600 && mustRefresh && changeCounter >= 200) {
            mustRefresh = false;
            changeCounter = 0;
            updateGraphs();
        }
    }

    public <T> void notifyRemoved(ObservableArrayList ts, int start, int count) {
        //
    }

    private void updateGraphs() {
        //THREAD
    }
}
