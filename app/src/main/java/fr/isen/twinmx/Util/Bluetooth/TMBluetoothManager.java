package fr.isen.twinmx.util.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;
import java.util.Observable;

import fr.isen.twinmx.R;
import fr.isen.twinmx.Receivers.BluetoothIconReceiver;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.util.Bluetooth.SmoothBluetoothFork.TMDevice;
import fr.isen.twinmx.util.Bluetooth.SmoothBluetoothFork.TMSmoothBluetooth;
import fr.isen.twinmx.util.TMBluetoothDialogAdapter;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 10/5/2016.
 */

public class TMBluetoothManager {

    private TMBluetooth bluetooth;


/*    private static TMBluetoothManager mBluetoothManagerInstance = null;
    public static TMBluetoothManager makeInstance(Activity activity) {
        return (mBluetoothManagerInstance = new TMBluetoothManager(activity));
    }

    public static TMBluetoothManager getInstance() {
        return mBluetoothManagerInstance;
    }*/

    public TMBluetoothManager(Activity activity) {
        this.bluetooth = new TMBluetooth(activity);
    }

    public TMBluetooth getBluetooth() {
        return this.bluetooth;
    }


}
