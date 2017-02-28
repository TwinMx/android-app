package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.ui.adapters.ManualAdapter;
import fr.isen.twinmx.utils.manual.ManualPage;
import fr.isen.twinmx.utils.manual.ReadFileHelper;

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

    @BindView(R.id.cardview_twinmax)
    CardView cardTwinMax;

    @BindView(R.id.cardview_app)
    CardView cardApp;

    @BindView(R.id.manual_Layout)
    ScrollView manualAppLayout;

    private List<ManualPage> twinmaxPages;

    private ManualFragmentEnum selectedCard = ManualFragmentEnum.NONE;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
        {
            this.selectedCard = ManualFragmentEnum.convertIntValueToEnum(savedInstanceState.getInt("ManualFragment"));

            if (ManualFragmentEnum.APP.equals(this.selectedCard))
            {
                this.loadAppManual();
            }
            else if (ManualFragmentEnum.TM.equals((this.selectedCard)))
            {
               this.loadTwinmaxManual();
            }
        }
    }

    @OnClick(R.id.cardview_app)
    public void onCardViewApp()
    {
        if (!ManualFragmentEnum.APP.equals(this.selectedCard)) this.loadAppManual();
    }

    @OnClick(R.id.cardview_twinmax)
    public void onCardViewTwinMax()
    {
        if (!ManualFragmentEnum.TM.equals(this.selectedCard)) this.loadTwinmaxManual();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("ManualFragment", selectedCard.getValue());
    }

    private void loadTwinmaxManual()
    {
        if (this.twinmaxPages == null)
        {
            this.twinmaxPages= ReadFileHelper.getManualFromFile(TMApplication.getContext(), "Twinmax");
        }

        this.manualAdapter.setItems(this.twinmaxPages);
        manualTitle.setText(getString(R.string.twinmax_title));

        cardTwinMax.setAlpha(1f);
        cardApp.setAlpha(0.5f);

        this.selectedCard = ManualFragmentEnum.TM;
        this.manualAppLayout.setVisibility(View.GONE);
        this.instructions.setVisibility(View.VISIBLE);
    }

    private void loadAppManual()
    {
        manualTitle.setText(getString(R.string.app_title));
        cardTwinMax.setAlpha(0.5f);
        cardApp.setAlpha(1f);

        this.selectedCard = ManualFragmentEnum.APP;
        this.manualAppLayout.setVisibility(View.VISIBLE);
        this.instructions.setVisibility(View.GONE);
    }

    private enum ManualFragmentEnum
    {
        NONE(0),
        TM(1),
        APP(2);

        private final int value;

        ManualFragmentEnum(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }

        public static ManualFragmentEnum convertIntValueToEnum(int value)
        {
            switch(value)
            {
                case 0:
                    return ManualFragmentEnum.NONE;
                case 1:
                    return ManualFragmentEnum.TM;
                case 2:
                    return ManualFragmentEnum.APP;
                default:
                    return null;
            }
        }
    }
}
