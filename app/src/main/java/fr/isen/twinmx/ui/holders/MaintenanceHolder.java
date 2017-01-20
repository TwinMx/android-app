package fr.isen.twinmx.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.R;
import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.listeners.OnMotoMaintenanceClickListener;

/**
 * Created by pierredfc.
 */

public class MaintenanceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.maintenance_date)
    TextView maintenanceDate;

    private OnMotoMaintenanceClickListener callback;

    private Maintenance maintenance;

    public MaintenanceHolder(View itemView, OnMotoMaintenanceClickListener callback) {
        super(itemView);
        this.callback = callback;
        itemView.setOnClickListener(this);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Maintenance maintenance)
    {
        this.maintenance = maintenance;
        this.maintenanceDate.setText(this.maintenance.getDate());
    }

    @Override
    public void onClick(View view) {
        this.callback.onMotoMaintenanceClick(this.maintenance);
    }
}
