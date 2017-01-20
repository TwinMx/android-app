package fr.isen.twinmx.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.isen.twinmx.R;
import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.listeners.OnMotoMaintenanceClickListener;
import fr.isen.twinmx.ui.holders.AcquisitionHolder;

/**
 * Created by pierredfc.
 */

public class AcquisitionAdapter extends RecyclerView.Adapter<AcquisitionHolder> {

    private List<Maintenance> maintenances;

    private OnMotoMaintenanceClickListener callback;

    public AcquisitionAdapter(List<Maintenance> maintenances, OnMotoMaintenanceClickListener callback)
    {
        this.maintenances = maintenances;
        this.callback = callback;
    }

    @Override
    public AcquisitionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.maintenance_item, parent, false);
        return new AcquisitionHolder(v, this.callback);
    }

    @Override
    public void onBindViewHolder(AcquisitionHolder holder, int position) {
        if (position < this.getItemCount()) {
            holder.bind(this.maintenances.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return this.maintenances.size();
    }

    public List<Maintenance> getMaintenances() {
        return maintenances;
    }

    public void setMaintenances(List<Maintenance> maintenances)
    {
        this.maintenances = maintenances;
        notifyDataSetChanged();
    }
}
