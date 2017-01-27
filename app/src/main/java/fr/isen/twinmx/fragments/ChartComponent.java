package fr.isen.twinmx.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.isen.twinmx.R;
import fr.isen.twinmx.model.MeasuresList;

/**
 * Created by Clement on 19/01/2017.
 */

public class ChartComponent implements IChartComponent, OnChartGestureListener, OnChartValueSelectedListener {

    private final LineChart chart;
    private final MeasuresList measuresList;
    private final Context context;
    private LineDataSet[] sets;
    private int[] colors;
    private int refreshRate;


    public ChartComponent(Context context, LineChart chart, MeasuresList measuresList, int[] colors) {
        this.chart = chart;
        this.measuresList = measuresList;
        this.colors = colors;
        this.context = context;
        this.refreshRate = this.measuresList.getRefreshRate();
    }

    public void onResume() {
        this.initChart();
    }

    private void initChart() {
        this.sets = new LineDataSet[4];
        for (int i = 0; i < 4; i++) {
            sets[i] = getLine(100, i, colors[i]);
        }
        this.updateChart(sets);

        this.chart.getAxisRight().setEnabled(false);
        this.chart.setTouchEnabled(true);

        this.chart.setOnChartGestureListener(this);
        this.chart.setOnChartValueSelectedListener(this);
        this.chart.setDrawGridBackground(false);
    }


    private void updateChart(LineDataSet[] sets) {
        LineData lineData = new LineData(sets);
        this.chart.setData(lineData);
    }

    public void setVisible(int index, boolean show) {
        this.chart.getLineData().getDataSetByIndex(index).setVisible(show);
        this.refresh();
    }

    public void refresh() {
        this.chart.invalidate();
    }


    public void update() {


        this.updateChart(this.sets);
        for (LineDataSet set : this.sets) {
            set.notifyDataSetChanged();
        }
        this.chart.notifyDataSetChanged();
        this.refresh();
    }

    @Override
    public void feedMultiple() {

    }

    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }

    public void fitScreen() {
        if (this.chart != null) {
            this.chart.fitScreen();
        }
    }

    @Override
    public void onCreate() {

    }

    public LineDataSet getLine(int n, int index, int color) {
        LineDataSet dataSet = new LineDataSet(this.measuresList.getLineEntries(index)/*getRandomEntries(n)*/, this.context.getString(R.string.cylinder, index + 1));
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
