package fr.isen.twinmx.util.Bluetooth;

import android.util.Log;

import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.model.Measures;
import fr.isen.twinmx.model.RawMeasure;
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




    public TMBluetooth(TMBluetoothDataManager dataManager, TMBluetoothListener listener) {
        super(TMApplication.getContext(), connectionTo, connectionType, listener);
        this.dataManager = dataManager;
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

    public void onDataReceived(int data) {
        this.dataManager.addRawData(data);
    }


}
