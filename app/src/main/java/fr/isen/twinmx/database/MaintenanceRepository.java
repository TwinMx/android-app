package fr.isen.twinmx.database;

import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.database.model.Repository;

/**
 * Created by pierredfc.
 */

public class MaintenanceRepository extends Repository<Maintenance> {

    private static MaintenanceRepository instance = null;

    public static MaintenanceRepository getInstance() {
        if (instance == null) {
            instance = new MaintenanceRepository();
        }
        return instance;
    }

    private MaintenanceRepository() {
        super(Maintenance.class);
    }
}
