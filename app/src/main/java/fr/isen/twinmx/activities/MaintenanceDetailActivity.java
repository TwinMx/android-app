package fr.isen.twinmx.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.repositories.MotoRepository;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.database.model.Moto;

import fr.isen.twinmx.model.TMDataSets;
import fr.isen.twinmx.utils.TMUtils;

/**
 * Activity that displays a maintenance
 */
public class MaintenanceDetailActivity extends AppCompatActivity {

    /**
     * Motorcycle's identifier
     */
    private long motoId;

    /**
     * Maintenance's index
     */
    private int maintenanceIndex;

    /**
     * Data used on the chart
     */
    private TMDataSets dataSets;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.maintenance_graph)
    LineChart graph;

    @BindView(R.id.maintenance_note)
    TextView note;

    @OnClick({R.id.box1, R.id.box2, R.id.box3, R.id.box4})
    public void onBoxClick(View view) {
        final Integer index = Integer.valueOf((String) view.getTag());

        if (view instanceof AppCompatCheckBox)
        {
            final AppCompatCheckBox box = (AppCompatCheckBox) view;
            this.graph.getLineData().getDataSetByIndex(index).setVisible(box.isChecked());
            this.graph.notifyDataSetChanged();
            this.graph.invalidate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maintenance_menu, menu);
        return true;
    }

    public static Intent makeIntent(Context context, Moto moto, Maintenance maintenance) {
        return makeIntent(context, moto.getId(), moto.getMaintenances().indexOf(maintenance));
    }

    public static Intent makeIntent(Context context, Long id, int maintenanceIndex) {
        final Intent intent = new Intent(context, MaintenanceDetailActivity.class);
        intent.putExtra(TMUtils.MOTO_ID_INTENT, id);
        intent.putExtra(TMUtils.MAINTENANCE_INDEX_INTENT, maintenanceIndex);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_maintenance_detail);

        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);

        if (this.getSupportActionBar() != null)
        {
            final Drawable upArrow = ContextCompat.getDrawable(TMApplication.getContext(), R.drawable.ic_arrow_back_white_24dp);
            this.getSupportActionBar().setHomeAsUpIndicator(upArrow);
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        final Bundle extras = this.getIntent().getExtras();
        this.motoId = extras.getLong(TMUtils.MOTO_ID_INTENT);
        this.maintenanceIndex =  extras.getInt(TMUtils.MAINTENANCE_INDEX_INTENT);

        final Maintenance maintenance = MotoRepository.getInstance().findById(this.motoId).getMaintenances().get(this.maintenanceIndex);

        this.setTitle(DateFormat.getDateTimeInstance().format(new Date(Long.valueOf(maintenance.getDate()))));

        if (!maintenance.getNote().isEmpty())
        {
            this.note.setText(maintenance.getNote());
        }
        else
        {
            this.note.setText(TMApplication.getContext().getString(R.string.no_note));
        }

        this.dataSets = new TMDataSets(this, this.graph, 4, 200);
        this.initChartSettings();
        this.dataSets.load(maintenance.getGraphs());
    }

    public void onResume() {
        super.onResume();
        this.dataSets.refreshChart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_maintenance:
                this.deleteMaintenance();
                break;
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete a maintenance
     */
    private void deleteMaintenance()
    {
        try {
            final MotoRepository instance = MotoRepository.getInstance();
            instance.deleteMaintenance(instance.findById(this.motoId), this.maintenanceIndex);
            this.finish();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the chart
     */
    private void initChartSettings() {
        this.graph.getAxisRight().setEnabled(false);
        this.graph.getXAxis().setDrawLabels(false);
        this.graph.setDrawGridBackground(false);
        this.graph.setDescription(new Description() {{
            setText(getString(R.string.pressure));
        }});
        this.graph.getLegend().setEnabled(false);
        this.graph.getAxisRight().setAxisMinimum(0);
    }
}



