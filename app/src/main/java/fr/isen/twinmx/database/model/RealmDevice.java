package fr.isen.twinmx.database.model;

import fr.isen.twinmx.database.interfaces.AutoIncrement;
import fr.isen.twinmx.util.bluetooth.SmoothBluetoothFork.TMDevice;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Clement on 27/01/2017.
 */

public class RealmDevice extends RealmObject implements AutoIncrement {

    public static final String DB_TYPE = "RealmDevice";

    @PrimaryKey @Required
    private Long id = null;

    @Required
    private String address;

    @Required
    private String name;

    public RealmDevice() {

    }

    public RealmDevice(TMDevice device) {
        this(device.getName(), device.getAddress());
    }

    public RealmDevice(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("%1$s (%2$s)", name, address);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RealmDevice) {
            RealmDevice device = (RealmDevice) obj;
            return this.getAddress().equals(device.getAddress());
        }
        else if (obj instanceof TMDevice) {
            TMDevice device = (TMDevice) obj;
            return this.getAddress().equals(device.getAddress());
        }
        else {
            return super.equals(obj);
        }
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
