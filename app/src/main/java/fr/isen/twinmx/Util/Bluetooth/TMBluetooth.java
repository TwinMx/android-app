package fr.isen.twinmx.Util.Bluetooth;

import android.content.Context;

import fr.isen.twinmx.Fragments.BluetoothFragment;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 9/27/2016.
 */

public class TMBluetooth extends SmoothBluetooth {

    private static final ConnectionTo connectionTo = ConnectionTo.OTHER_DEVICE;
    private static final Connection connectionType = Connection.INSECURE;

    private TMBluetoothListener bluetoothListener;

    public TMBluetooth(BluetoothFragment bluetoothFragment, TMBluetoothListener listener) {
        super(bluetoothFragment.getActivity().getApplicationContext(), connectionTo, connectionType, listener);
        this.bluetoothListener = listener;
    }
}
