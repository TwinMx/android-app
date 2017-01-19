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
import android.util.Log;
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
import java.util.Observable;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;

/**
 * Created by pierredfc.
 */
public class ChartFragment extends BluetoothFragment {

    private Context context;
    private LineChart chart;
    private LineData lineData;
    private IChartComponent chartComponent;

    @OnClick({R.id.box1, R.id.box2, R.id.box3, R.id.box4})
    public void onBoxClick(View view) {
        Integer index = Integer.valueOf((String) view.getTag());

        this.chartComponent.setVisible(index, ((AppCompatCheckBox) view).isChecked());

/*        if (((AppCompatCheckBox) view).isChecked()) {
            chart.getLineData().getDataSetByIndex(index).setVisible(true);
        } else {
            chart.getLineData().getDataSetByIndex(index).setVisible(false);
        }*/
    }

    private boolean isStarted;

    @OnClick(R.id.match_start_pause)
    public void onControlsClick(View view) {
        ImageView image = (ImageView) view;

        if (this.isStarted) {
            this.chartComponent.pause();
            image.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_play_arrow_white_24dp));
        } else {
            this.chartComponent.play();
            image.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_pause_white_24dp));
        }

        this.isStarted = !this.isStarted;

        // TODO
    }

    @OnClick(R.id.auto_focus)
    public void onAutoFocusClick(View view) {
        this.chartComponent.fitScreen();
    }

    @OnClick(R.id.save_acquisition)
    public void onSaveClick(View view) {
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

        LineChart chart = (LineChart) rootView.findViewById(R.id.graph);

/*        this.chartComponent = new ChartComponent(this.getActivity(), (LineChart) rootView.findViewById(R.id.graph), this.getMeasuresList(),
                new int[]{
                        ContextCompat.getColor(this.getActivity(), R.color.chartBlue),
                        ContextCompat.getColor(this.getActivity(), R.color.chartGreen),
                        ContextCompat.getColor(this.getActivity(), R.color.chartBrown),
                        ContextCompat.getColor(this.getActivity(), R.color.chartRed)
                });*/


        this.chartComponent = new RealTimeChartComponent(this.getActivity(), chart);
        this.chartComponent.onCreate();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isStarted = true;

    }

    public void onResume() {
        super.onResume();
        this.chartComponent.onResume();
        this.setMotorLifeCycle();
    }

    private void setMotorLifeCycle() {
        this.motorLifeCycle.addSeries(new SeriesItem.Builder(ContextCompat.getColor(this.getActivity(), R.color.white))
                .setRange(minValue, maxValue, maxValue)
                .setInitialVisibility(true)
                .setLineWidth(10f)
                .setDrawAsPoint(false)
                .build());

        this.motorLifeCycle.configureAngles(280, 0);

        final SeriesItem seriesItem1 = new SeriesItem.Builder(ContextCompat.getColor(this.getActivity(), R.color.colorPrimary), ContextCompat.getColor(this.getActivity(), R.color.colorAccent))
                .setRange(minValue, maxValue, (minValue + maxValue) / 2)
                .setLineWidth(6f)
                .build();

        serie1Index = this.motorLifeCycle.addSeries(seriesItem1);
    }

    @Override
    public CoordinatorLayout getCoordinatorLayout() {
        return null;
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d("Observer", "Update: " + TMBluetoothManager.getInstance().getDataManager().getData().size());
        this.chartComponent.update();
    }
}
