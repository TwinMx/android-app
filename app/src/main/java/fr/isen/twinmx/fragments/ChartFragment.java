package fr.isen.twinmx.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.animation.AccelerateInterpolator;
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
import butterknife.OnLongClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.fragments.chart.TMChart;
import fr.isen.twinmx.fragments.chart.TriggerManager;
import fr.isen.twinmx.listeners.OnPeriodListener;
import fr.isen.twinmx.model.ChartBundle;
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

    /**
     * Properties
     **/

    private Context context;
    private int maxMotorValue = 8000;
    private int minMotorValue = 0;
    private TMChart chartComponent;
    private boolean playing = false;
    private static Boolean wasPlaying = null;
    private boolean isCalibrationEnabled = true;

    private static final String STATE_PLAYING = "STATE_PLAYING";
    private static final String STATE_CURVE_1_ENABLED = "STATE_CURVE_1_ENABLED";
    private static final String STATE_CURVE_2_ENABLED = "STATE_CURVE_2_ENABLED";
    private static final String STATE_CURVE_3_ENABLED = "STATE_CURVE_3_ENABLED";
    private static final String STATE_CURVE_4_ENABLED = "STATE_CURVE_4_ENABLED";
    private static final String STATE_CALIBRATION_BTN_ENABLED = "STATE_CALIBRATION_BTN_ENABLED";

    private MaterialDialog chooseMotoDialog;
    private AcquisitionSaveRequest acquisitionSaveRequest = null;

    @BindView(R.id.box1)
    AppCompatCheckBox checkBoxCurveOne;

    @BindView(R.id.box2)
    AppCompatCheckBox checkBoxCurveTwo;

    @BindView(R.id.box3)
    AppCompatCheckBox checkBoxCurveThree;

    @BindView(R.id.box4)
    AppCompatCheckBox checkBoxCurveFour;

    @BindView(R.id.match_start_pause)
    ImageView playPauseImage;

    @BindView(R.id.refresh)
    ImageView calibrationButton;

    @BindView(R.id.motorLifeCycleValue)
    TextView motorLifeCycleValue;

    @BindView(R.id.motorLifeCycle)
    DecoView motorLifeCycle;

    /**
     * Listeners
     **/

    @OnLongClick(R.id.refresh)
    public boolean onLongCalibrationClick(View view) {
        if (view instanceof ImageView) {
            ImageView v = (ImageView) view;

            if (isCalibrationEnabled) {
                this.chartComponent.disableCalibration();
                v.setBackground(ContextCompat.getDrawable(TMApplication.getContext(), R.drawable.greyripple));
            } else {
                v.setBackground(ContextCompat.getDrawable(TMApplication.getContext(), R.drawable.revertripple));
                this.chartComponent.enableCalibration();
            }

            this.isCalibrationEnabled = !isCalibrationEnabled;
        }
        return true;
    }

    @OnClick(R.id.refresh)
    public void onCalibrationClick(View view) {
        if (this.isPlaying()) {
            this.chartComponent.resetCalibration();
            if (!this.isCalibrationEnabled) {
                ImageView v = (ImageView) view;
                v.setBackground(ContextCompat.getDrawable(TMApplication.getContext(), R.drawable.revertripple));
                this.isCalibrationEnabled = true;
            }

        }
    }

    @OnClick({R.id.box1, R.id.box2, R.id.box3, R.id.box4})
    public void onBoxClick(View view) {
        Integer index = Integer.valueOf((String) view.getTag());
        this.chartComponent.setVisible(index, ((AppCompatCheckBox) view).isChecked());
    }

    @OnClick(R.id.match_start_pause)
    public void onControlsClick(View view) {
        this.playPause();
    }

    @OnClick(R.id.auto_focus)
    public void onAutoFocusClick(View view) {
        this.chartComponent.fitScreen();
    }

    @OnClick(R.id.save_acquisition)
    public void onSaveClick(View view) {
        pauseBeforeSaving();
        this.acquisitionSaveRequest = this.chartComponent.createAcquisitionSaveRequest();
        if (this.acquisitionSaveRequest != null) {
            showChooseMotoDialog();
        }
    }




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

        this.chartComponent = new TMChart(this.getActivity(),
                this,
                chart,
                getBluetooth(),
                savedInstanceState != null ? new ChartBundle(savedInstanceState) : null);
        this.chartComponent.onCreate();

        TriggerManager triggerManager = this.chartComponent.getTriggerManager();
        triggerManager.addOnPeriodListener(this);

        if (savedInstanceState != null) {
            this.setCheckBoxState(0, savedInstanceState, STATE_CURVE_1_ENABLED, this.checkBoxCurveOne);
            this.setCheckBoxState(1, savedInstanceState, STATE_CURVE_2_ENABLED, this.checkBoxCurveTwo);
            this.setCheckBoxState(2, savedInstanceState, STATE_CURVE_3_ENABLED, this.checkBoxCurveThree);
            this.setCheckBoxState(3, savedInstanceState, STATE_CURVE_4_ENABLED, this.checkBoxCurveFour);
            this.setCalibrationButtonState(savedInstanceState, this.calibrationButton);
            this.wasPlaying = savedInstanceState.getBoolean(STATE_PLAYING);
        }

        return rootView;
    }

    private void setCheckBoxState(int index, Bundle savedInstanceState, String key, AppCompatCheckBox checkbox) {
        if (savedInstanceState.containsKey(key)) {
            final boolean isChecked = savedInstanceState.getBoolean(key, true);
            checkbox.setChecked(isChecked);
            this.chartComponent.setVisible(index, isChecked);
        }
    }

    private void setCalibrationButtonState(Bundle savedInstanceState, ImageView refresh) {
        if (savedInstanceState.containsKey(STATE_CALIBRATION_BTN_ENABLED)) {
            final boolean isEnabled = savedInstanceState.getBoolean(STATE_CALIBRATION_BTN_ENABLED);
            if (!isEnabled) {
                refresh.setBackground(ContextCompat.getDrawable(TMApplication.getContext(), R.drawable.greyripple));
            }
            isCalibrationEnabled = isEnabled;
        }
    }

    public void onResume() {
        super.onResume();
        this.setPlayPauseImage(this.playPauseImage, this.getActivity(), this.wasPlaying);
        this.chartComponent.onResume(this.wasPlaying);
        this.setMotorLifeCycle();
    }

    private void setMotorLifeCycle() {
        this.motorLifeCycle.addSeries(new SeriesItem.Builder(ContextCompat.getColor(this.getActivity(), R.color.white))
                .setRange(minMotorValue, maxMotorValue, 0)
                .setInitialVisibility(true)
                .setLineWidth(10f)
                .setInterpolator(new AccelerateInterpolator())
                .setDrawAsPoint(false)
                .build());

        this.motorLifeCycle.configureAngles(280, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.wasPlaying = this.isPlaying();
        this.chartComponent.pause(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        this.chartComponent.onStop();
    }

    @Override
    public CoordinatorLayout getCoordinatorLayout() {
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_PLAYING, this.wasPlaying);
        outState.putBoolean(STATE_CURVE_1_ENABLED, checkBoxCurveOne.isChecked());
        outState.putBoolean(STATE_CURVE_2_ENABLED, checkBoxCurveTwo.isChecked());
        outState.putBoolean(STATE_CURVE_3_ENABLED, checkBoxCurveThree.isChecked());
        outState.putBoolean(STATE_CURVE_4_ENABLED, checkBoxCurveFour.isChecked());
        outState.putBoolean(STATE_CALIBRATION_BTN_ENABLED, isCalibrationEnabled);
        this.chartComponent.save(outState);
        super.onSaveInstanceState(outState);
    }

    public void setContext(Context context) {
        this.context = context;
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
                .title(getString(R.string.save))
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
                        chartComponent.play();
                    }
                })
                .negativeText(R.string.form_cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        acquisitionSaveRequest = null;
                        chartComponent.play();
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
    public void onPeriod(long nbPointsSinceLastPeriod) {
        // TwinMax send the data every 600us and we display the data every 4 received data
        double period = nbPointsSinceLastPeriod * 600 * 4;
        // We need to convert us to min
        period = period * (0.000001 / 60);

        // We need turn by minute and one period equals 2 turns
        final double compte_tour = 2 / period;

        // Update views
        this.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Integer value = (int) compte_tour;
                motorLifeCycleValue.setText(String.valueOf(value));
                final SeriesItem seriesItem1 = new SeriesItem.Builder(ContextCompat.getColor(getActivity(), R.color.colorPrimary), ContextCompat.getColor(getActivity(), R.color.colorAccent))
                        .setRange(minMotorValue, maxMotorValue, value)
                        .setLineWidth(6f)
                        .build();
                motorLifeCycle.deleteAll();
                motorLifeCycle.addSeries(seriesItem1);

            }
        });
    }

    public void playPause() {
        if (this.isPlaying()) {
            this.pause();
        }
        else {
            this.play();
        }
    }

    public void play() {
        boolean hasConnectedDevice = this.getBluetooth().getConnectedDevice() != null || this.getBluetooth().hasConnectedFile();
        if (!hasConnectedDevice) {
            return;
        }
        setPauseImage(this.playPauseImage, context); //next possible icon is 'pause'
        this.chartComponent.play();
        this.setPlaying(true);
    }

    public void pause() {
        setPlayImage(this.playPauseImage, context); //next possible icon 'play'
        this.chartComponent.pause(false);
        this.setPlaying(false);
    }

    private void pauseBeforeSaving() {
        this.chartComponent.pause(true);
        this.setPauseImage(this.playPauseImage, this.context);
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    private void setPlayPauseImage(ImageView image, Context context, Boolean value) {
        if (value == null) {
            return;
        }

        if (value) {
            setPauseImage(image, context);
            this.setPlaying(true);
        } else {
            setPlayImage(image, context);
            this.setPlaying(false);
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
}
