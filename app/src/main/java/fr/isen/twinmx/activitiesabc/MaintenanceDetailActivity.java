package fr.isen.twinmx.activitiesabc;

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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.database.model.RealmFloat;
import fr.isen.twinmx.database.model.RealmGraph;
import fr.isen.twinmx.fragments.LimitedEntryList;
import fr.isen.twinmx.fragments.RealTimeChartComponent;
import io.realm.RealmList;

/**
 * Created by pierredfc.
 */

public class MaintenanceDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.maintenance_graph)
    LineChart graph;

    @BindView(R.id.maintenance_note)
    TextView note;

    @OnClick({R.id.box1, R.id.box2, R.id.box3, R.id.box4})
    public void onBoxClick(View view) {
        Integer index = Integer.valueOf((String) view.getTag());

        if (view instanceof AppCompatCheckBox)
        {
            AppCompatCheckBox box = (AppCompatCheckBox) view;
            this.graph.getLineData().getDataSetByIndex(index).setVisible(box.isChecked());
            this.graph.notifyDataSetChanged();
            this.graph.invalidate();
        }
    }

    private long motoId;

    private int maintenanceIndex;

    private ArrayList<LimitedEntryList> dataSetEntries = new ArrayList<>(4);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maintenance_menu, menu);
        return true;
    }

    public static Intent makeIntent(Context context, Moto moto, Maintenance maintenance) {
        return makeIntent(context, moto.getId(), moto.getMaintenances().indexOf(maintenance));
    }

    public static Intent makeIntent(Context context, Long id, int maintenanceIndex) {
        Intent intent = new Intent(context, MaintenanceDetailActivity.class);
        intent.putExtra("motoId", id);
        intent.putExtra("maintenanceIndex", maintenanceIndex);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_detail);

        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);

        if (this.getSupportActionBar() != null)
        {
            final Drawable upArrow = ContextCompat.getDrawable(TMApplication.getContext(), R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = this.getIntent().getExtras();
        this.motoId = extras.getLong("motoId");
        this.maintenanceIndex =  extras.getInt("maintenanceIndex");

        Maintenance maintenance = MotoRepository.getInstance().findById(motoId).getMaintenances().get(maintenanceIndex);

        this.setTitle(DateFormat.getDateTimeInstance().format(new Date(Long.valueOf(maintenance.getDate()))));

        if (!maintenance.getNote().isEmpty())
        {
            this.note.setText(maintenance.getNote());
        }
        else
        {
            this.note.setText(TMApplication.getContext().getString(R.string.no_note));
        }

        graph.setData(new LineData());
        initChartSettings();
        loadGraph(maintenance.getGraphs());
    }

    private void loadGraph(RealmList<RealmGraph> graphs)
    {
        for (int index = 0; index < graphs.size(); index++)
        {
            RealmGraph graph = graphs.get(index);

            for (RealmFloat data : graph.getMeasures())
            {
                addEntry(index, new Entry(index, data.getValue()));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_maintenance:
                deleteMaintenance();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteMaintenance()
    {
        try {
            MotoRepository instance = MotoRepository.getInstance();
            instance.deleteMaintenance(instance.findById(this.motoId), this.maintenanceIndex);
            finish();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    private void initChartSettings() {
        graph.getAxisRight().setEnabled(false);
        graph.getXAxis().setDrawLabels(false);
        for(int index = 0; index < 4; index++) {
            dataSetEntries.add(null);
        }
        graph.setDrawGridBackground(false);
        graph.setDescription(new Description() {{
            setText("Pression (mBar)");
        }});
        graph.getLegend().setEnabled(false);
    }

    public void addEntry(int index, Entry value) {
        LineData data = graph.getData();

        if (data != null) {
            LimitedEntryList entries = this.dataSetEntries.get(index);
            if (entries == null) {
                entries = addNewSet(TMApplication.getContext().getString(R.string.cylinder, index + 1), index);
                this.dataSetEntries.set(index, entries);
            }

            synchronized(entries) {
                data.addEntry(value, index);
            }
            data.notifyDataChanged();
        }
    }

    private LimitedEntryList addNewSet(String title, int index) {

        int color = 0;
        switch (index) {
            case 0:
                color = R.color.chartBlue;
                break;
            case 1:
                color = R.color.chartGreen;
                break;
            case 2:
                color = R.color.chartBrown;
                break;
            case 3:
                color = R.color.chartRed;
                break;
            default:
                color = R.color.chartBlue;
                break;
        }

        color = ContextCompat.getColor(TMApplication.getContext(), color);

        LimitedEntryList entries = new LimitedEntryList(RealTimeChartComponent.NB_POINTS);

        LineDataSet dataSet = new LineDataSet(entries, title);
        dataSet.setColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(0);
        dataSet.setValueTextColor(color);
        dataSet.setCircleColor(color);

        graph.getData().addDataSet(dataSet);
        return entries;
    }
}



