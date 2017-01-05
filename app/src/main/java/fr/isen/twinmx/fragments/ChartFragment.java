package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

import fr.isen.twinmx.R;

/**
 * Created by pierredfc.
 */
public class ChartFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener{

    private LineChart chart;
    private Context context;
    private LineData lineData;


    public static ChartFragment newInstance(Context context, LineData lineData) {
        final ChartFragment chartFragment = new ChartFragment();
        chartFragment.context = context;
        chartFragment.lineData = lineData;
        return chartFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_chart, container, false);

//        this.chart = new LineChart(this.context);
        this.chart = (LineChart) rootView.findViewById(R.id.graph);

        this.chart.setData(this.lineData);

        //options data0
        this.setupChart(0, Color.rgb(237, 127, 16));
        //options data1
        this.setupChart(1, Color.rgb(58, 142, 186));
        //options data2
        this.setupChart(2, Color.rgb(127, 221, 76));
        //options data3
        this.setupChart(3, Color.rgb(247, 35, 12));

        return rootView;
    }

    private void setupChart(int index, int color)
    {
        if (this.chart.getLineData().getDataSetByIndex(index) instanceof LineDataSet)
        {
            final LineDataSet lineDataSet = (LineDataSet) this.chart.getLineData().getDataSetByIndex(index);

            //lineDataSet.setDrawCubic(true);
            lineDataSet.setValueTextSize(0);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setColor(color);
        }

        this.chart.setOnChartGestureListener(this);
        this.chart.setOnChartValueSelectedListener(this);
        this.chart.setDrawGridBackground(false);

        // enable touch gestures
        this.chart.setTouchEnabled(true);

        // enable scaling and dragging
        this.chart.setDragEnabled(true);


        // if disabled, scaling can be done on x- and y-axis separately
        this.chart.setPinchZoom(true);

        this.setData(45, 100);
    }

    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (this.chart.getData() != null &&
                this.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)this.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            this.chart.getData().notifyDataChanged();
            this.chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            // set the line to be drawn like this "- - - - - -"
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);

           /* set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);*/

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this.getActivity(), R.drawable.material_drawer_badge);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            this.chart.setData(data);
        }
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
