package fr.isen.twinmx.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import fr.isen.twinmx.R;
import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.database.model.Moto;

/**
 * Created by pierredfc.
 */

public class MaintenanceDetailActivity extends AppCompatActivity {

    private Maintenance maintenance;

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

        // TODO
        Bundle extras = this.getIntent().getExtras();
        Long motoId = extras.getLong("motoId");
        Integer maintenanceIndex =  extras.getInt("maintenanceIndex");

        Maintenance maintenance = MotoRepository.getInstance().findById(motoId).getMaintenances().get(maintenanceIndex);

        Log.d("Maintenance", ""+maintenance);
    }
}
