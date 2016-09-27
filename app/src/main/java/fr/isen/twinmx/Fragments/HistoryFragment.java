package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.isen.twinmx.activities.MainActivity;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.async.GetHistoryAyncTask;
import fr.isen.twinmx.listeners.RequestListener;
import fr.isen.twinmx.model.History;
import fr.isen.twinmx.ui.adapters.HistoryAdapter;

/**
 * Created by pierredfc.
 */

public class HistoryFragment extends Fragment implements RequestListener {

    private View rootview;

    private RecyclerView historyView;

    private TextView noHistoryView;

    private HistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.rootview = inflater.inflate(R.layout.fragment_history, container, false);

        this.historyView = (RecyclerView) this.rootview.findViewById(R.id.history_recycler);
        this.noHistoryView = (TextView) this.rootview.findViewById(R.id.no_history_text);

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_history));

        this.historyView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(TMApplication.getContext());
        this.historyView.setLayoutManager(layoutManager);

        return this.rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        final GetHistoryAyncTask getHistoryAyncTask = new GetHistoryAyncTask(this);
        getHistoryAyncTask.execute();
    }

    @Override
    public void onResponseReceived(List<History> results) {
        if (results.size() == 0) {
            this.noHistoryView.setVisibility(View.VISIBLE);
        } else {
            this.historyAdapter = new HistoryAdapter(results, (MainActivity) getActivity());
            this.historyView.setAdapter(this.historyAdapter);
        }
    }
}
