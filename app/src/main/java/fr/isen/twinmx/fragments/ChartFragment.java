package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.AppCompatCheckBox;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;

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
public class ChartFragment extends BluetoothFragment implements OnChartGestureListener, OnChartValueSelectedListener{

    private LineChart chart;
    private Context context;
    private LineData lineData;

    @OnClick({R.id.box1, R.id.box2, R.id.box3, R.id.box4})
    public void onBoxClick(View view)
    {
        Integer index = Integer.valueOf((String) view.getTag());

        if (((AppCompatCheckBox) view).isChecked()) {
            chart.getLineData().getDataSetByIndex(index).setVisible(true);
        } else {
            chart.getLineData().getDataSetByIndex(index).setVisible(false);
        }

        chart.invalidate();
    }

    private boolean isStarted;

    @OnClick(R.id.match_start_pause)
    public void onControlsClick(View view)
    {
        ImageView image = (ImageView) view;

        if (this.isStarted)
        {
            image.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_play_arrow_white_24dp));
        }
        else
        {
            image.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_pause_white_24dp));
        }

        this.isStarted = !this.isStarted;

        // TODO
    }

    @OnClick(R.id.auto_focus)
    public void onAutoFocusClick(View view)
    {
        if (this.chart != null)
        {
            this.chart.fitScreen();
        }
    }

    @OnClick(R.id.save_acquisition)
    public void onSaveClick(View view)
    {
        // TODO
    }

    @BindView(R.id.motorLifeCycleValue)
    TextView motorLifeCycleValue;

    @BindView(R.id.motorLifeCycle)
    DecoView motorLifeCycle;

    private int serie1Index;
    private int maxValue = 5000;
    private int minValue = 0;

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

        LineDataSet l1 = getLine(100, 1, ContextCompat.getColor(this.getActivity(), R.color.chartBlue));
        LineDataSet l2 = getLine(100, 2, ContextCompat.getColor(this.getActivity(), R.color.chartGreen));
        LineDataSet l3 = getLine(100, 3, ContextCompat.getColor(this.getActivity(), R.color.chartBrown));
        LineDataSet l4 = getLine(100, 4, ContextCompat.getColor(this.getActivity(), R.color.chartRed));

        LineData lineData = new LineData(l1, l2, l3, l4);
        this.chart.setData(lineData);

        this.chart.getAxisRight().setEnabled(false);
        this.chart.setTouchEnabled(true);

        this.chart.setOnChartGestureListener(this);
        this.chart.setOnChartValueSelectedListener(this);
        this.chart.setDrawGridBackground(false);

        this.setMotorLifeCycle();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isStarted = true;
    }

    public LineDataSet getLine(int n, int index, int color) {
        LineDataSet dataSet = new LineDataSet(getRandomEntries(n), getString(R.string.cylinder, index));
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

    private void setMotorLifeCycle()
    {
        this.motorLifeCycle.addSeries(new SeriesItem.Builder(ContextCompat.getColor(this.getActivity(), R.color.white))
                .setRange(minValue, maxValue, maxValue)
                .setInitialVisibility(true)
                .setLineWidth(10f)
                .setDrawAsPoint(false)
                .build());

        this.motorLifeCycle.configureAngles(280, 0);

        final SeriesItem seriesItem1 = new SeriesItem.Builder(ContextCompat.getColor(this.getActivity(), R.color.colorPrimary), ContextCompat.getColor(this.getActivity(), R.color.colorAccent))
                .setRange(minValue, maxValue, (minValue+maxValue)/2)
                .setLineWidth(6f)
                .build();

        serie1Index = this.motorLifeCycle.addSeries(seriesItem1);
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

    @Override
    public CoordinatorLayout getCoordinatorLayout() {
        return null;
    }
}
