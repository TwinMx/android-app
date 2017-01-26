package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.activities.MainActivity;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.ui.adapters.ManualAdapter;
import fr.isen.twinmx.ui.adapters.MotosAdapter;
import fr.isen.twinmx.util.manual.ManualPage;
import fr.isen.twinmx.util.manual.ReadFileHelper;

/**
 * Created by pierredfc.
 */

public class ManualFragment extends Fragment {

    private View rootview;

    private ManualAdapter manualAdapter;

    @BindView(R.id.manual_recycler)
    RecyclerView instructions;

    @BindView(R.id.manual_title)
    TextView manualTitle;

    @OnClick(R.id.cardview_app)
    public void onCardViewApp(View view)
    {
        List<ManualPage> inst = ReadFileHelper.getManualFromFile(TMApplication.getContext(), "Application");
        this.manualAdapter.setItems(inst);
    }

    @OnClick(R.id.cardview_twinmax)
    public void onCardViewTwinMax(View view)
    {
        List<ManualPage> inst = ReadFileHelper.getManualFromFile(TMApplication.getContext(), "Twinmax");
        this.manualAdapter.setItems(inst);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootview = inflater.inflate(R.layout.fragment_manual, container, false);

        ButterKnife.bind(this, rootview);

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_instruction));

        this.instructions.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(TMApplication.getContext());
        this.instructions.setLayoutManager(layoutManager);
        this.instructions.setAdapter(this.manualAdapter = new ManualAdapter(new ArrayList<ManualPage>(0)));
        return this.rootview;
    }

}
