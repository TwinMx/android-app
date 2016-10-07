package fr.isen.twinmx.util.Bluetooth;

import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

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
        this.bluetooth = new TMBluetooth(this.bluetoothListener);
    }


    public void showBluetoothDevicesDialog(List<Device> devices, SmoothBluetooth.ConnectionCallback connectionCallback) {
        if (this.bluetoothDevicesDialog == null || !this.bluetoothDevicesDialog.isShowing()) {

            this.bluetoothDevicesDialog = new MaterialDialog.Builder(this.activity)
                    .title(TMApplication.getContext().getResources().getString(R.string.select_bt_device))
                    .adapter(new TMBluetoothDialogAdapter(devices, connectionCallback), null)
                    .build();

            this.bluetoothDevicesDialog.show();
        }
    }

    public void hideBluetoothDevicesDialog() {
        this.bluetoothDevicesDialog.hide();
        this.bluetoothDevicesDialog = null;
    }

    public TMBluetooth getBluetooth() {
        return this.bluetooth;
    }
}
