package fr.isen.twinmx.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.AppCompatCheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.activities.MainActivity;
import fr.isen.twinmx.util.Bluetooth.TMBluetooth;

/**
 * Created by pierredfc.
 */
public class ChartFragment extends BluetoothFragment {

    private Context context;
    private int maxMotorValue = 5000;
    private int minMotorValue = 0;
    private RealTimeChartComponent chartComponent;
    private int serie1Index;
    private boolean playing = false;

    @BindView(R.id.match_start_pause)
    ImageView playPauseImage;

    private Boolean onResumeWasPlaying = null;

    private static final String STATE_PLAYING = "STATE_PLAYING";

    @OnClick({R.id.box1, R.id.box2, R.id.box3, R.id.box4})
    public void onBoxClick(View view) {
        Integer index = Integer.valueOf((String) view.getTag());

        this.chartComponent.setVisible(index, ((AppCompatCheckBox) view).isChecked());
    }

    private boolean isStarted;

    @OnClick(R.id.match_start_pause)
    public void onControlsClick(View view) {
        ImageView image = (ImageView) view;

        if (this.isStarted) {
            setPlayImage(image, context);
            this.chartComponent.pause(true);
        } else {
            setPauseImage(image, context);
            this.chartComponent.play(true);
        }

        this.isStarted = !this.isStarted;

        // TODO
    }

    private void setPlayPauseImage(ImageView image, Context context, Boolean value) {
        if (value == null) {
            return;
        }

        if (value) {
            setPauseImage(image, context);
            this.isStarted = true;
        }
        else {
            setPlayImage(image, context);
            this.isStarted = false;
        }
    }

    private void setPlayImage(ImageView image, Context context) {
        if (context != null) image.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_play_arrow_white_24dp));
    }

    private void setPauseImage(ImageView image, Context context) {
        if (context != null) image.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_pause_white_24dp));
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

    public static ChartFragment newInstance(Context context, TMBluetooth bluetooth) {
        final ChartFragment chartFragment = new ChartFragment();
        chartFragment.context = context;
        chartFragment.setBluetooth(bluetooth);
        return chartFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_chart, container, false);

        ButterKnife.bind(this, rootView);

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_acquisition));

        LineChart chart = (LineChart) rootView.findViewById(R.id.graph);

        this.chartComponent = new RealTimeChartComponent(this.getActivity(), this, chart, getBluetooth());
        this.chartComponent.onCreate();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isStarted = true;

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_PLAYING)) {
            this.onResumeWasPlaying = savedInstanceState.getBoolean(STATE_PLAYING);
        } else {
            this.onResumeWasPlaying = null;
        }
    }

    public void onResume() {
        super.onResume();
        this.setPlayPauseImage(this.playPauseImage, this.getActivity(), this.onResumeWasPlaying);
        this.chartComponent.onResume(this.onResumeWasPlaying, true);
        this.setMotorLifeCycle();
    }

    private void setMotorLifeCycle() {
        this.motorLifeCycle.addSeries(new SeriesItem.Builder(ContextCompat.getColor(this.getActivity(), R.color.white))
                .setRange(minMotorValue, maxMotorValue, maxMotorValue)
                .setInitialVisibility(true)
                .setLineWidth(10f)
                .setDrawAsPoint(false)
                .build());

        this.motorLifeCycle.configureAngles(280, 0);

        final SeriesItem seriesItem1 = new SeriesItem.Builder(ContextCompat.getColor(this.getActivity(), R.color.colorPrimary), ContextCompat.getColor(this.getActivity(), R.color.colorAccent))
                .setRange(minMotorValue, maxMotorValue, (minMotorValue + maxMotorValue) / 2)
                .setLineWidth(6f)
                .build();

        serie1Index = this.motorLifeCycle.addSeries(seriesItem1);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.chartComponent.pause(false);
    }

    @Override
    public CoordinatorLayout getCoordinatorLayout() {
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_PLAYING, isPlaying());
        super.onSaveInstanceState(outState);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
