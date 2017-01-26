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

    private final TMBluetoothDataManager dataManager;

    private Device connectedDevice = null;
    private TMBluetoothListener bluetoothListener;


    public TMBluetooth(TMBluetoothDataManager dataManager, TMBluetoothListener listener) {
        super(TMApplication.getContext(), connectionTo, connectionType, listener);
        this.dataManager = dataManager;
        listener.setBluetooth(this);
        this.bluetoothListener = listener;
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

    public void onDataReceived(int data) {
        this.dataManager.addFrame(data);
    }


    public TMBluetoothListener getListener() {
        return bluetoothListener;
    }
}
