package fr.isen.twinmx.util.Bluetooth;

import android.content.Context;

import fr.isen.twinmx.fragments.BluetoothFragment;
import fr.isen.twinmx.TMApplication;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 9/27/2016.
 */

public class TMBluetooth extends SmoothBluetooth {

    private static final ConnectionTo connectionTo = ConnectionTo.OTHER_DEVICE;
    private static final Connection connectionType = Connection.INSECURE;

    private TMBluetoothListener bluetoothListener;

    public TMBluetooth(TMBluetoothListener listener) {
        super(TMApplication.getContext(), connectionTo, connectionType, listener);
        this.bluetoothListener = listener;
    }
}
