package fr.isen.twinmx.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.AppCompatCheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.fragments.chart.RealTimeChartComponent;
import fr.isen.twinmx.fragments.chart.TriggerManager;
import fr.isen.twinmx.listeners.OnPeriodListener;
import fr.isen.twinmx.model.InitChartData;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.listeners.OnMotoHistoryClickListener;
import fr.isen.twinmx.model.AcquisitionSaveRequest;
import fr.isen.twinmx.ui.adapters.DialogMotoAdapter;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

/**
 * Created by pierredfc.
 */
public class ChartFragment extends BluetoothFragment implements OnMotoHistoryClickListener, OnPeriodListener {

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
    private static final String STATE_NB_GRAPHS = "STATE_NB_GRAPHS";
    private static final String STATE_GRAPH = "STATE_GRAPH_";
    private static final String STATE_GRAPH_SIZE = "STATE_GRAPH_SIZE";
    private static final String STATE_TRIGGER = "STATE_TRIGGER";
    private static final String STATE_CALIBRATION_WIDTH = "STATE_CALIBRATION_WIDTH";


    private MaterialDialog chooseMotoDialog;
    private AcquisitionSaveRequest acquisitionSaveRequest = null;


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
            this.chartComponent.pause(false, true);
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
        } else {
            setPlayImage(image, context);
            this.isStarted = false;
        }
    }

    private void setPlayImage(ImageView image, Context context) {
        if (context != null)
            image.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_play_arrow_white_24dp));
    }

    private void setPauseImage(ImageView image, Context context) {
        if (context != null)
            image.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_pause_white_24dp));
    }

    @OnClick(R.id.auto_focus)
    public void onAutoFocusClick(View view) {
        this.chartComponent.fitScreen();
    }

    @OnClick(R.id.save_acquisition)
    public void onSaveClick(View view) {
        this.chartComponent.pause(true, true);
        this.setPauseImage(this.playPauseImage, this.context);
        List<LimitedEntryList> entries = this.chartComponent.getDataSetEntries();
        if (entries != null && entries.size() > 0 && entries.get(0) != null && entries.get(0).size() > 0) {
            this.acquisitionSaveRequest = new AcquisitionSaveRequest(entries);
            showChooseMotoDialog();
        }
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

        this.chartComponent = new RealTimeChartComponent(this.getActivity(), this, chart, getBluetooth(), savedInstanceState != null ? new InitChartData(savedInstanceState, STATE_NB_GRAPHS, STATE_GRAPH_SIZE, STATE_GRAPH, STATE_TRIGGER, STATE_CALIBRATION_WIDTH) : null);
        this.chartComponent.onCreate();

        TriggerManager triggerManager = this.chartComponent.getTriggerManager();
        triggerManager.addOnPeriodListener(this);

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
        this.chartComponent.pause(false, false);
    }

    @Override
    public CoordinatorLayout getCoordinatorLayout() {
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_PLAYING, isPlaying());
        int nbGraphs = this.chartComponent.getNbGraphs();
        outState.putInt(STATE_NB_GRAPHS, nbGraphs);
        outState.putInt(STATE_GRAPH_SIZE, this.chartComponent.getGraphsSize());
        for (int i = 0; i < nbGraphs; i++) {
            outState.putFloatArray(STATE_GRAPH + i, this.chartComponent.getDataSetValues(i));
        }
        try {
            outState.putFloat(STATE_TRIGGER, this.chartComponent.getTriggerManager().getTriggeredDataSet().getTrigger());
        } catch(Exception ex) {
            //
        }

        try {
            outState.putLong(STATE_CALIBRATION_WIDTH, this.chartComponent.getCalibrationManager().getTwoPeriods());
        } catch(Exception ex) {
            //
        }
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

    public void onMotoHistoryClick(Moto moto) {
        this.acquisitionSaveRequest.setMoto(moto);
        hideChooseMotoDialog();
        showSaveAcquistionDialog(moto);
    }

    private void showChooseMotoDialog() {
        if (this.chooseMotoDialog == null || !this.chooseMotoDialog.isShowing()) {
            List<Moto> motos = new LinkedList<>();
            motos.addAll(MotoRepository.getInstance().findAll());

            this.chooseMotoDialog = new MaterialDialog.Builder(this.getActivity())
                    .title(TMApplication.getContext().getResources().getString(R.string.select_moto))
                    .adapter(new DialogMotoAdapter(motos, this), null)
                    .build();

            this.chooseMotoDialog.show();
        }
    }

    private void hideChooseMotoDialog() {
        if (this.chooseMotoDialog != null && this.chooseMotoDialog.isShowing()) {
            this.chooseMotoDialog.dismiss();
            this.chooseMotoDialog = null;
        }
    }

    private void showSaveAcquistionDialog(Moto moto) {
        MaterialDialog dialog = new MaterialDialog.Builder(this.getActivity())
                .title("Sauvegarde de l'acquisition")
                .customView(R.layout.form_acquisition_save, true)
                .positiveText(R.string.form_save)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText motoNameEditText = (EditText) dialog.getCustomView().findViewById(R.id.dialog_acquistion_moto_name);
                        EditText noteEditText = (EditText) dialog.getCustomView().findViewById(R.id.dialog_acquistion_note);

                        String motoName = motoNameEditText.getText().toString();
                        String note = noteEditText.getText().toString();

                        if (acquisitionSaveRequest.isNewMoto()) {
                            acquisitionSaveRequest.createNewMoto(motoName);
                        }

                        acquisitionSaveRequest.setNote(note);
                        acquisitionSaveRequest.save();
                        chartComponent.play(true);
                    }
                })
                .negativeText(R.string.form_cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        acquisitionSaveRequest = null;
                        chartComponent.play(true);
                    }
                })
                .build();

        if (moto != null) {
            try {
                View customView = dialog.getCustomView();
                assert customView != null;
                customView.findViewById(R.id.dialog_acquisition_moto_name_label).setVisibility(View.GONE);
                customView.findViewById(R.id.dialog_acquistion_moto_name).setVisibility(View.GONE);
            } catch (NullPointerException ex) {
                //
            }

        }

        dialog.show();
    }

    @Override
    public void onPeriodListener(long nbPointsSinceLastPeriod) {
        // TwinMax send the data every 600us and we display the data every 4 received data
        double period = nbPointsSinceLastPeriod * 600 * 4;
        // We need to convert us to min
        period = period * (0.000001/60);

        // We need turn by minute and one period equals 2 turns
        final double compte_tour = 2 / period;

        // Update views
        this.getActivity().runOnUiThread (new Thread(new Runnable() {
            public void run() {
                motorLifeCycleValue.setText(String.valueOf((int) compte_tour));
                final SeriesItem seriesItem1 = new SeriesItem.Builder(ContextCompat.getColor(getActivity(), R.color.colorPrimary), ContextCompat.getColor(getActivity(), R.color.colorAccent))
                        .setRange(minMotorValue, maxMotorValue, (int) compte_tour)
                        .setLineWidth(6f)
                        .build();

                serie1Index = motorLifeCycle.addSeries(seriesItem1);
            }
        }));
    }
}
