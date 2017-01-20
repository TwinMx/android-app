package fr.isen.twinmx.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import fr.isen.twinmx.R;
import fr.isen.twinmx.database.model.Maintenance;

/**
 * Created by pierredfc.
 */

public class MaintenanceDetailActivity extends AppCompatActivity {

    private Maintenance maintenance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_detail);

        // TODO
        Long maintenanceID =  this.getIntent().getExtras().getLong("maintenanceID");
    }
}
