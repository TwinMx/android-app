package fr.isen.twinmx.util.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.isen.twinmx.R;
import fr.isen.twinmx.Receivers.BluetoothIconReceiver;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.RealmDeviceRepository;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.model.RealmDevice;
import fr.isen.twinmx.util.Bluetooth.SmoothBluetoothFork.TMBluetoothService;
import fr.isen.twinmx.util.Bluetooth.SmoothBluetoothFork.TMDevice;
import fr.isen.twinmx.util.Bluetooth.SmoothBluetoothFork.TMSmoothBluetooth;
import fr.isen.twinmx.util.TMBluetoothDialogAdapter;
import io.palaima.smoothbluetooth.Device;

/**
 * Created by cdupl on 9/27/2016.
 */

public class TMBluetooth extends TMSmoothBluetooth implements TMSmoothBluetooth.Listener {

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int RESULT_ENABLE_BT_ALLOWED = -1;
    public static final int RESULT_ENABLE_BT_CANCELLED = 0;

    private static final ConnectionTo connectionTo = ConnectionTo.OTHER_DEVICE;
    private static final Connection connectionType = Connection.INSECURE;

    private final TMBluetoothDataManager dataManager;
    private final Activity activity;
    private BluetoothIconReceiver bluetoothIconReceiver;
    private MaterialDialog bluetoothDevicesDialog;


    public TMBluetooth(Activity activity) {
        super(TMApplication.getContext(), connectionTo, connectionType, null);
        this.setListener(this);
        this.dataManager = new TMBluetoothDataManager(1000);
        this.activity = activity;
    }

    /** Listeners **/
    @Override
    public void onBluetoothNotSupported() {
        Log.d("TMBluetooth", "onBluetoothNotSupported");
        if (this.bluetoothIconReceiver != null) {
            this.bluetoothIconReceiver.errorOrDisabled();
        }
    }

    @Override
    public void onBluetoothNotEnabled() {
        Log.d("TMBluetooth", "onBluetoothNotEnabled");
        if (this.bluetoothIconReceiver != null) {
            this.bluetoothIconReceiver.disabled();
        }
    }

    @Override
    public void onConnecting(TMDevice device) {
        Log.d("TMBluetooth", "onConnecting ["+device+"]");
        if (this.bluetoothIconReceiver != null) {
            this.bluetoothIconReceiver.connecting();
        }
    }

    @Override
    public void onConnected(TMDevice device) {
        Log.d("TMBluetooth", "onConnected ["+device+"]");
        if (this.bluetoothIconReceiver != null) {
            this.bluetoothIconReceiver.connected(TMApplication.loadString(R.string.connected_to, device.getName()));
        }

        device.setTwinMax(true);
        try {
            RealmDeviceRepository.getInstance().create(new RealmDevice(device));
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        //BluetoothIconReceiver.sendStatusOk(String.format(TMApplication.getContext().getResources().getString(R.string.connected_to), device.getName()));
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public void onDisconnected() {
        Log.d("TMBluetooth", "onDisconnected ["+this.getConnectedDevice()+"]");
        if (this.bluetoothIconReceiver != null) {
            this.bluetoothIconReceiver.disconnected();
        }
    }

    @Override
    public void onConnectionFailed(TMDevice device) {
        Log.d("TMBluetooth", "onConnectionFailed ["+device+"]");
        //BluetoothIconReceiver.sendStatusError(String.format(TMApplication.getContext().getResources().getString(R.string.connection_failed_to),device.getName()));
        if (this.bluetoothIconReceiver != null) {
            this.bluetoothIconReceiver.error(TMApplication.loadString(R.string.connection_failed_to));
        }
    }

    @Override
    public void onDiscoveryStarted() {
        Log.d("TMBluetooth", "onDiscoveryStarted");

    }

    @Override
    public void onDiscoveryFinished() {
        Log.d("TMBluetooth", "onDiscoveryFinished");

    }

    @Override
    public void onNoDevicesFound() {
        Log.d("TMBluetooth", "onNoDevicesFound");
    }

    @Override
    public void onDevicesFound(List<TMDevice> deviceList, ConnectionCallback connectionCallback) {
        for(TMDevice device : deviceList) {
            if (RealmDeviceRepository.getInstance().contains(device)) {
                device.setTwinMax(true);
            }
        }
        Collections.sort(deviceList, new Comparator<TMDevice>() {
            @Override
            public int compare(TMDevice o1, TMDevice o2) {
                if (o1.isTwinMax()) {
                    return -1;
                }
                else if (o2.isTwinMax()) {
                    return 1;
                }
                return 0;
            }
        });
        this.showBluetoothDevicesDialog(deviceList, connectionCallback);
    }

    private void showBluetoothDevicesDialog(List<TMDevice> devices, ConnectionCallback connectionCallback) {
        if (!this.isBluetoothEnabled()) {
            return;
        }

        BluetoothIconReceiver.sendStatusEnabled();

        if (this.bluetoothDevicesDialog == null || !this.bluetoothDevicesDialog.isShowing()) {

            this.bluetoothDevicesDialog = new MaterialDialog.Builder(this.activity)
                    .title(TMApplication.getContext().getResources().getString(R.string.select_bt_device))
                    .adapter(new TMBluetoothDialogAdapter(devices, connectionCallback, this), null)
                    .build();

            this.bluetoothDevicesDialog.show();
        }
    }

    public void hideBluetoothDevicesDialog() {
        if (this.bluetoothDevicesDialog != null) {
            this.bluetoothDevicesDialog.dismiss();
            this.bluetoothDevicesDialog = null;
        }
    }

    public void onDataReceived(int data) {
        this.dataManager.addFrame(data);
    }

    @Override
    public void setupService() {
        this.setTMBluetoothService(new TMBluetoothService(getmHandler(), dataManager));
    }

    public TMBluetoothDataManager getDataManager() {
        return dataManager;
    }

    public void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        this.activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        this.tryConnection();
    }

    public boolean isBluetoothEnabled() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            return adapter.isEnabled();
        }
        return false;
    }

    public boolean isBluetoothConnectedToDevice() {
        return this.isConnected();
    }

    public void connectToKnownDevicesOrScanDevices() {
        this.tryConnection();
    }

    public void setBluetoothIconReceiver(BluetoothIconReceiver bluetoothIconReceiver) {
        this.bluetoothIconReceiver = bluetoothIconReceiver;
    }

    public void scanDevices() {
        this.tryConnection();
    }
}
