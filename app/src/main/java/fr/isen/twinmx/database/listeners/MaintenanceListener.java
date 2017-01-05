package fr.isen.twinmx.database.listeners;

import fr.isen.twinmx.database.model.Maintenance;

/**
 * Created by pierredfc.
 */

public interface MaintenanceListener {
    void addMaintenance(Maintenance maintenance, onAddMaintenanceCallback callback);
    void removeMaintenance(int maintenanceId, onRemoveMaintenanceCallback callback);

    interface onAddMaintenanceCallback
    {
        void onSuccess();
        void onFailure();
    }

    interface onRemoveMaintenanceCallback
    {
        void onSuccess();
        void onFailure();
    }
}
