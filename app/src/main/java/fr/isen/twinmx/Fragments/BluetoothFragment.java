package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.R;

/**
 * Created by pierredfc.
 */
public class BluetoothFragment extends Fragment {

    @BindView(R.id.bt_list)
    RecyclerView recyclerView;

    @BindView(R.id.bt_connected_to)
    TextView connectedTo;

    @BindView(R.id.disconnect_bt)
    Button disconnectButton;

    @BindView(R.id.start_bt)
    Button startButton;

    private View rootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootview = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        ButterKnife.bind(this.getActivity());

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_acquisition));

        return this.rootview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean wrapInScrollView = true;
        new MaterialDialog.Builder(this.getActivity())
                .title(R.string.bnav_acquisition)
                .customView(R.layout.custom_form, wrapInScrollView)
                .positiveText(R.string.form_save)
                .negativeText(R.string.form_cancel)
                .show();
    }
}
