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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.activities.MainActivity;
import fr.isen.twinmx.R;
import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.exceptions.MotoRepositoryException;
import fr.isen.twinmx.database.listeners.MotoListener;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.listeners.RequestListener;
import fr.isen.twinmx.model.History;
import fr.isen.twinmx.ui.adapters.HistoryAdapter;
import fr.isen.twinmx.ui.adapters.MotosAdapter;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Created by pierredfc.
 */

public class HistoryFragment extends Fragment implements MotoListener.OnCreateMotoCallback, RequestListener {

    private View rootview;

    @BindView(R.id.history_recycler)
    RecyclerView historyView;

    @BindView(R.id.no_history_text)
    TextView noHistoryView;


    private HistoryAdapter historyAdapter;
    private MotosAdapter motosAdapter;
    private RealmResults<Moto> motoFinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.rootview = inflater.inflate(R.layout.fragment_history, container, false);

/*        this.historyView = (RecyclerView) this.rootview.findViewById(R.id.history_recycler);
        this.noHistoryView = (TextView) this.rootview.findViewById(R.id.no_history_text);*/

        ButterKnife.bind(this, rootview);

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_history));

        this.historyView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(TMApplication.getContext());
        this.historyView.setLayoutManager(layoutManager);
        this.historyView.setAdapter(this.motosAdapter = new MotosAdapter(new ArrayList<Moto>(0)));
        return this.rootview;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        MotoRepository repository = MotoRepository.getInstance();

        demo(repository);


    }

    @Override
    public void onResume() {
        super.onResume();
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

    public void onMotoResponseReceived(List<Moto> motos) {
        if (motos != null && !motos.isEmpty()) {
            this.motosAdapter.setItems(motos);
        }
    }

    public void demo(MotoRepository repository) {
        try {
            repository.deleteAll();
        } catch (MotoRepositoryException e) {
            e.printStackTrace();
        }

        for (int i = 1; i < 40; i++) {
            try {
                repository.create(new Moto("Moto"+i));
            } catch (MotoRepositoryException e) {
                e.printStackTrace();
            }
        }
    }
}
