package fr.isen.twinmx.Fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.R;

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

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Activer le bluetooth ?", Snackbar.LENGTH_INDEFINITE)
                .setAction("Oui", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        View view = snackbar.getView();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);

        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.blue500));

    // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);

    // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
