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
import android.widget.Toast;
import java.util.List;
import fr.isen.twinmx.activities.MainActivity;
import fr.isen.twinmx.R;
import fr.isen.twinmx.database.RealmHelper;
import fr.isen.twinmx.database.listeners.CreateMotoListener;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.async.GetHistoryAyncTask;
import fr.isen.twinmx.listeners.RequestListener;
import fr.isen.twinmx.model.History;
import fr.isen.twinmx.ui.adapters.HistoryAdapter;


/**
 * Created by pierredfc.
 */

public class HistoryFragment extends Fragment implements CreateMotoListener.OnCreateMotoCallback, RequestListener {

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
    public void onStart()
    {
        super.onStart();

        final GetHistoryAyncTask getHistoryAyncTask = new GetHistoryAyncTask(this);
        getHistoryAyncTask.execute();

     //   RealmHelper realmHelper = new RealmHelper();
     //   realmHelper.getMotos();
     //    realmHelper.createMoto(new Moto("Harley", new Date().toString()), this);
    }

    @Override
    public void onSuccess()
    {
        Toast.makeText(this.getActivity(), "onSuccess", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure()
    {
        Toast.makeText(this.getActivity(), "onFailure", Toast.LENGTH_SHORT).show();
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
}
