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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.ui.adapters.ManualAdapter;
import fr.isen.twinmx.utilabc.manual.ManualPage;
import fr.isen.twinmx.utilabc.manual.ReadFileHelper;

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

    private ManualFragmentEnum selectedCard = ManualFragmentEnum.NONE;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
        {
            this.selectedCard = ManualFragmentEnum.convertIntValueToEnum(savedInstanceState.getInt("ManualFragment"));

            if (ManualFragmentEnum.APP.equals(this.selectedCard))
            {
                this.loadInstructions("Application", R.string.app_title, 0.5f, 1f);
            }
            else if (ManualFragmentEnum.TM.equals((this.selectedCard)))
            {
                this.loadInstructions("Twinmax",R.string.twinmax_title, 1f, 0.5f);
            }
        }
    }

    @OnClick(R.id.cardview_app)
    public void onCardViewApp(View view)
    {
        this.loadInstructions("Application", R.string.app_title, 0.5f, 1f);
        this.selectedCard = ManualFragmentEnum.APP;
    }

    @OnClick(R.id.cardview_twinmax)
    public void onCardViewTwinMax(View view)
    {
        this.loadInstructions("Twinmax", R.string.twinmax_title, 1f, 0.5f);
        this.selectedCard = ManualFragmentEnum.TM;
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

    private void loadInstructions(String manual, int stringId, float alphaTM, float alphaApp)
    {
        List<ManualPage> pages = ReadFileHelper.getManualFromFile(TMApplication.getContext(), manual);
        this.manualAdapter.setItems(pages);
        manualTitle.setText(getString(stringId));

        cardTwinMax.setAlpha(alphaTM);
        cardApp.setAlpha(alphaApp);
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
