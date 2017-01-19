package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.activities.MainActivity;
import fr.isen.twinmx.R;
import fr.isen.twinmx.activities.MotoFormActivity;
import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.exceptions.MotoRepositoryException;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.listeners.MotoListener;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.listeners.OnMotoHistoryClickListener;
import fr.isen.twinmx.listeners.RequestListener;
import fr.isen.twinmx.model.History;
import fr.isen.twinmx.ui.adapters.HistoryAdapter;
import fr.isen.twinmx.ui.adapters.MotosAdapter;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Created by pierredfc.
 */

public class HistoryFragment extends Fragment implements MotoListener.OnCreateMotoCallback, RequestListener, View.OnClickListener {

    private View rootview;

    @BindView(R.id.history_recycler)
    RecyclerView historyView;

    @BindView(R.id.no_history_text)
    TextView noHistoryView;

    private HistoryAdapter historyAdapter;
    private MotosAdapter motosAdapter;
    private RealmResults<Moto> motoFinder;
    private MainActivity activity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
        {
            this.getActivity().findViewById(R.id.fab).setOnClickListener(this);
        }
    }

    public static HistoryFragment newInstance(MainActivity activity, FloatingActionButton fab) {
        HistoryFragment f = new HistoryFragment();
        f.activity = activity;
        fab.setOnClickListener(f);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.rootview = inflater.inflate(R.layout.fragment_history, container, false);

        ButterKnife.bind(this, rootview);

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_history));

        this.historyView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(TMApplication.getContext());
        this.historyView.setLayoutManager(layoutManager);
        this.historyView.setAdapter(this.motosAdapter = new MotosAdapter(new ArrayList<Moto>(0), (MainActivity) this.getActivity()));
        return this.rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        //demo(MotoRepository.getInstance());
        if (this.motoFinder != null) this.motoFinder.removeChangeListeners();
        this.motoFinder = MotoRepository.getInstance().findAllAsync(new RealmChangeListener<RealmResults<Moto>>() {
            @Override
            public void onChange(RealmResults<Moto> items) {
                onMotoResponseReceived(items);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.motoFinder != null) this.motoFinder.removeChangeListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSuccess()
    {

    }

    @Override
    public void onFailure()
    {

    }

    @Override
    public void onResponseReceived(List<History> results)
    {
        if (results != null && results.size() != 0)
        {
            this.historyAdapter = new HistoryAdapter(results, (MainActivity) getActivity());
            this.historyView.setAdapter(this.historyAdapter);
        }
        else
        {
            this.noHistoryView.setVisibility(View.VISIBLE);
        }
    }

    public void onMotoResponseReceived(List<Moto> motos) {
        if (motos != null && !motos.isEmpty()) {
            this.motosAdapter.setItems(motos);
        }
    }

    // On Floating Action Button click
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this.getActivity(), MotoFormActivity.class);
        startActivity(intent);
    }
}
