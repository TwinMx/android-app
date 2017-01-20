package fr.isen.twinmx.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.listeners.OnMotoMaintenanceClickListener;
import fr.isen.twinmx.ui.adapters.AcquisitionAdapter;
import fr.isen.twinmx.ui.adapters.MotosAdapter;
import fr.isen.twinmx.util.CircleTransformation;

/**
 * Created by pierredfc.
 */

public class MotoDetailActivity extends AppCompatActivity implements OnMotoMaintenanceClickListener {

    private Moto moto;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.moto_detail_photo)
    ImageView photo;

    @BindView(R.id.acquisition_recycler)
    RecyclerView acquisitions;

    private AcquisitionAdapter acquisitionAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.modify_moto:
                // TODO modify fragment
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.moto = MotoRepository.getInstance().findById(this.getIntent().getExtras().getLong("motoID"));

        setContentView(R.layout.activity_moto_detail);
        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);
        this.setTitle(this.moto.getName());

        Picasso.with(TMApplication.getContext())
                .load(moto.getImage())
                .transform(new CircleTransformation())
                .placeholder(R.drawable.ic_motorcycle_black_24dp)
                .into(this.photo);


        this.initRecyclerView();
    }

    private void initRecyclerView()
    {
        this.acquisitions.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(TMApplication.getContext());
        this.acquisitions.setLayoutManager(layoutManager);
        this.acquisitions.setAdapter(this.acquisitionAdapter = new AcquisitionAdapter(new ArrayList<Maintenance>(0), this));
    }

    @Override
    public void onMotoMaintenanceClick(Maintenance maintenance) {
        // TODO fragment with graph etc.
    }
}
