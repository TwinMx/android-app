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
import fr.isen.twinmx.util.TMBluetoothDialogAdapter;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 10/5/2016.
 */

public class TMBluetoothManager {

    private final Activity activity;
    private final TMBluetoothDataManager dataManager;
    private MaterialDialog bluetoothDevicesDialog;
    private TMBluetooth bluetooth;
    @SuppressWarnings("FieldCanBeLocal")
    private TMBluetoothListener bluetoothListener; //Keep a pointer to avoid GC

    private final static int REQUEST_ENABLE_BT = 1;

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
        this.dataManager = new TMBluetoothDataManager();
        this.bluetooth = new TMBluetooth(this.dataManager, this.bluetoothListener);
    }


    public void showBluetoothDevicesDialog(List<Device> devices, SmoothBluetooth.ConnectionCallback connectionCallback) {
        if (!this.bluetooth.isBluetoothEnabled()) {
            return;
        }

        BluetoothIconReceiver.sendStatusEnabled();

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

    public void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        this.activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        TMBluetoothManager.getInstance().getBluetooth().tryConnection();
    }



    public TMBluetoothDataManager getDataManager() {
        return dataManager;
    }
}
