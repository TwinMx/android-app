package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.R;
import fr.isen.twinmx.database.RealmHelper;
import fr.isen.twinmx.database.listeners.CreateMotoListener;
import fr.isen.twinmx.database.model.Moto;

/**
 * Created by pierredfc.
 */

public class HistoryFragment extends Fragment implements CreateMotoListener.OnCreateMotoCallback {

    private View rootview;

    @BindView(R.id.history)
    RecyclerView historyView;

    @BindView(R.id.no_history_text)
    TextView noHistoryView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootview = inflater.inflate(R.layout.fragment_history, container, false);

        ButterKnife.bind(this.getActivity());

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_history));

        return this.rootview;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        RealmHelper realmHelper = new RealmHelper();
        realmHelper.getMotos();
    //    realmHelper.createMoto(new Moto("Harley", new Date().toString()), this);
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this.getActivity(), "onSuccess", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure() {
        Toast.makeText(this.getActivity(), "onFailure", Toast.LENGTH_SHORT).show();
    }
}
