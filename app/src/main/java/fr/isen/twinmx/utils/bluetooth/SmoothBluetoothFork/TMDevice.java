package fr.isen.twinmx.utils.bluetooth.SmoothBluetoothFork;

import fr.isen.twinmx.model.enums.TMDeviceType;
import fr.isen.twinmx.model.TMInput;

/**
 * Created by Clement on 27/01/2017.
 */

public class TMDevice extends TMInput {
    private final String mAddress;
    private boolean isTwinMax = false; //true if we're sure, false otherwise

    public TMDevice(String name, String address, boolean paired) {
        super(TMDeviceType.BLUETOOTH, name, paired);
        mAddress = address;
    }

    public String getAddress() {
        return mAddress;
    }

    @Override
    public String toString() {
        return String.format("%1$s (%2$s)", getName(), mAddress);
    }

    public boolean isTwinMax() {
        return isTwinMax;
    }

    public void setTwinMax(boolean twinMax) {
        isTwinMax = twinMax;
    }
}
