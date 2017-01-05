package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

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
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;

/**
 * Created by pierredfc.
 */
public class ChartFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener{

    private LineChart chart;
    private Context context;
    private LineData lineData;

    @OnClick({R.id.box1, R.id.box2, R.id.box3, R.id.box4})
    public void onBoxClick(View view)
    {
        Integer index = Integer.valueOf((String) view.getTag());

        if (((CheckBox) view).isChecked()) {
            chart.getLineData().getDataSetByIndex(index).setVisible(true);
        } else {
            chart.getLineData().getDataSetByIndex(index).setVisible(false);
        }

        chart.invalidate();
    }

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

        ButterKnife.bind(this, rootView);

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_acquisition));

        this.chart = (LineChart) rootView.findViewById(R.id.graph);

        LineDataSet l1 = getLine(100, Color.RED);
        LineDataSet l2 = getLine(100, Color.GREEN);
        LineDataSet l3 = getLine(100, Color.BLUE);
        LineDataSet l4 = getLine(100, Color.YELLOW);

        LineData lineData = new LineData(l1, l2, l3, l4);
        this.chart.setData(lineData);

        this.chart.getAxisRight().setEnabled(false);
        this.chart.getAxisLeft().setDrawGridLines(false);
        this.chart.getXAxis().setDrawGridLines(false);
        this.chart.setTouchEnabled(true);

        this.chart.setOnChartGestureListener(this);
        this.chart.setOnChartValueSelectedListener(this);
        this.chart.setDrawGridBackground(false);

        return rootView;
    }

    public LineDataSet getLine(int n, int color) {
        LineDataSet dataSet = new LineDataSet(getRandomEntries(n), "Moto");
        dataSet.setColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(0);
        dataSet.setValueTextColor(color);
        dataSet.setCircleColor(color);
        return dataSet;
     }

    public List<Entry> getRandomEntries(int n) {
        List<Entry> entries = new ArrayList<>(n);
        Random random = new Random();
        int previousVal = 0;
        for (int i = 0; i < n; i++) {
            // turn your data into Entry objects
            int val = previousVal + random.nextInt(10) - 5;
            previousVal = val;
            entries.add(new Entry(i, val));
        }
        return entries;
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
