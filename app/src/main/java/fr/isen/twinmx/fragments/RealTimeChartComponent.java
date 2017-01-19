package fr.isen.twinmx.fragments;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.LinkedList;

import fr.isen.twinmx.R;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;

/**
 * Created by Clement on 19/01/2017.
 */

public class RealTimeChartComponent implements IChartComponent {

    private final Activity context;
    private final LineChart mChart;
    private Thread thread;

    public RealTimeChartComponent(Activity context, LineChart chart) {
        this.context = context;
        mChart = chart;
    }

    @Override
    public void setVisible(int index, boolean show) {

    }

    @Override
    public void fitScreen() {

    }

    public void onCreate() {
        mChart.setData(new LineData());
    }

    public void onResume() {
        feedMultiple();

    }

    @Override
    public void update() {

    }

    public void addEntry(int index, Entry value) {
        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = null;

            try {
                set = data.getDataSetByIndex(index);
            } catch (IndexOutOfBoundsException ex) {
                //
            }

            if (set == null) {
                set = createSet(this.context.getString(R.string.cylinder, index + 1), index);
                data.addDataSet(set);
            }

            int count = set.getEntryCount();
            while (count >= 120) {
                set.removeFirst();
                count--;
            }

            data.addEntry(value, index);

            data.notifyDataChanged();
        }
    }

    public void refreshChart() {
        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();

        // limit the number of visible entries
        mChart.setVisibleXRangeMaximum(120);
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // move to the latest entry
        mChart.moveViewToX(mChart.getData().getEntryCount());

        // this automatically refreshes the chart (calls invalidate())
        // mChart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
    }

    private LineDataSet createSet(String title, int index) {


        int color = 0;
        switch (index) {
            case 0:
                color = R.color.chartBlue;
                break;
            case 1:
                color = R.color.chartGreen;
                break;
            case 2:
                color = R.color.chartBrown;
                break;
            case 3:
                color = R.color.chartRed;
                break;
            default:
                color = R.color.chartBlue;
                break;
        }

        color = ContextCompat.getColor(this.context, color);

        LineDataSet dataSet = new LineDataSet(new LinkedList<Entry>() {{ add(new Entry(0, 0)); }}, title);
        dataSet.setColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(0);
        dataSet.setValueTextColor(color);
        dataSet.setCircleColor(color);

        return dataSet;
    }

    private void clearSets() {
        mChart.clear();
    }

    private LineAsyncTask a1;

    @Override
    public void feedMultiple() {
        play();
    }

    @Override
    public void play() {
        if (a1 != null) {
            a1.stop();
        }
        mChart.getData().clearValues();
        TMBluetoothManager.getInstance().getDataManager().start();
        a1 = new LineAsyncTask(this, TMBluetoothManager.getInstance());
        a1.execute(0);
    }

    @Override
    public void pause() {
        if (a1 != null) {
            a1.stop();
        }
        a1 = null;
        TMBluetoothManager.getInstance().getDataManager().stop();
    }


}
