package fr.isen.twinmx.util.Bluetooth;

import fr.isen.twinmx.TMApplication;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 9/27/2016.
 */

public class TMBluetooth extends SmoothBluetooth {

    private static final ConnectionTo connectionTo = ConnectionTo.OTHER_DEVICE;
    private static final Connection connectionType = Connection.INSECURE;

    private Device connectedDevice = null;

    public TMBluetooth(TMBluetoothListener listener) {
        super(TMApplication.getContext(), connectionTo, connectionType, listener);
        listener.setBluetooth(this);
    }

    public Device getConnectedDevice() {
        if (this.isConnected()) {
            return this.connectedDevice;
        }
        return null;
    }

    protected void setConnectedDevice(Device device) {
        this.connectedDevice = device;
    }
}
