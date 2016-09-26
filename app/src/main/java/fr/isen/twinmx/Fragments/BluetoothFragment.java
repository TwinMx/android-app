package fr.isen.twinmx.Fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.Activities.MainActivity;
import fr.isen.twinmx.R;
import fr.isen.twinmx.Util.TMSnackBar;

/**
 * Created by pierredfc.
 */
public class BluetoothFragment extends Fragment {

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
    public void onResume() {
        super.onResume();

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) this.getActivity().findViewById(R.id.bluetoothContainer);

        Snackbar snackbar = TMSnackBar
                .makeBluetooth(this.getActivity().getApplicationContext(), coordinatorLayout, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        snackbar.show();
    }
}
