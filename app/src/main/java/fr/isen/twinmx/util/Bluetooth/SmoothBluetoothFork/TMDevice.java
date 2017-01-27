package fr.isen.twinmx.util.Bluetooth.SmoothBluetoothFork;

/**
 * Created by Clement on 27/01/2017.
 */

public class TMDevice {
    private final String mName;
    private final String mAddress;
    private final boolean mPaired;

    public TMDevice(String name, String address, boolean paired) {
        mName = name;
        mAddress = address;
        mPaired = paired;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public boolean isPaired() {
        return mPaired;
    }

    @Override
    public String toString() {
        return String.format("%1$s (%2$s)", mName, mAddress);
    }
}
