package fr.isen.twinmx.util.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;
import java.util.Set;

import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.util.TMBluetoothDialogAdapter;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 10/5/2016.
 */

public class TMBluetoothManager {

    private final Activity activity;
    private MaterialDialog bluetoothDevicesDialog;
    private TMBluetooth bluetooth;
    private TMBluetoothListener bluetoothListener; //Keep a pointer to avoid GC


    private static TMBluetoothManager mBluetoothManagerInstance = null;
    public static TMBluetoothManager makeInstance(Activity activity) {
        return (mBluetoothManagerInstance = new TMBluetoothManager(activity));
    }

    public static TMBluetoothManager getInstance() {
        return mBluetoothManagerInstance;
    }

    private TMBluetoothManager(Activity activity) {
        this.activity = activity;
        this.bluetoothListener = new TMBluetoothListener();
        this.bluetooth = new TMBluetooth(bluetoothListener);
    }

    private final BroadcastReceiver mBluetoothBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                Log.d("Discovered",device.getName() + " | " + device.getAddress());
            }
        }
    };

/*    public void showPairedBluetoothDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                Log.d("Device", device.getName());
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }*/

    public void discoverBluetoothDevices() {
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mBluetoothBroadcastReceiver, filter); // Don't forget to unregister during onDestroy
    }

    public void showBluetoothDevicesDialog(List<Device> devices, SmoothBluetooth.ConnectionCallback connectionCallback) {
        if (bluetoothDevicesDialog == null || !bluetoothDevicesDialog.isShowing()) {

            bluetoothDevicesDialog = new MaterialDialog.Builder(this.activity)
                    .title(TMApplication.getContext().getResources().getString(R.string.select_bt_device))
                    .adapter(new TMBluetoothDialogAdapter(devices, connectionCallback), null)
                    .build();

            bluetoothDevicesDialog.show();
        }
    }

    public void hideBluetoothDevicesDialog() {
        bluetoothDevicesDialog.hide();
        bluetoothDevicesDialog = null;
    }


    public TMBluetooth getBluetooth() {
        return bluetooth;
    }
}
