package fr.isen.twinmx.database;

import java.util.List;

import fr.isen.twinmx.database.model.RealmDevice;
import fr.isen.twinmx.database.model.Repository;
import fr.isen.twinmx.util.bluetooth.SmoothBluetoothFork.TMDevice;

/**
 * Created by Clement on 27/01/2017.
 */

public class RealmDeviceRepository extends Repository<RealmDevice> {

    private static RealmDeviceRepository instance = null;

    public static RealmDeviceRepository getInstance() {
        if (instance == null) {
            instance = new RealmDeviceRepository();
        }
        return instance;
    }

    public RealmDeviceRepository() {
        super(RealmDevice.class);
    }

    public List<RealmDevice> findByAddress(String address) {
        List<RealmDevice> all = findAll();
        List<RealmDevice> devices = getRepository().equalTo("address", address).findAll();
        return devices;
    }

    public boolean contains(TMDevice device) {
        return !findByAddress(device.getAddress()).isEmpty();

    }
}
