package fr.isen.twinmx.database.model;

import fr.isen.twinmx.util.Bluetooth.SmoothBluetoothFork.TMDevice;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Clement on 27/01/2017.
 */

public class RealmBluetoothDevice extends RealmObject {

    @PrimaryKey
    private String address;

    @Required
    private String name;

    public RealmBluetoothDevice(TMDevice device) {
        this(device.getName(), device.getAddress());
    }

    public RealmBluetoothDevice(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("%1$s (%2$s)", name, address);
    }

}
