package fr.isen.twinmx.util.Bluetooth;

import android.util.Log;

import java.util.List;
import java.util.Observable;

import fr.isen.twinmx.R;
import fr.isen.twinmx.Receivers.BluetoothIconReceiver;
import fr.isen.twinmx.TMApplication;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 9/27/2016.
 */

public class TMBluetoothListener extends Observable implements SmoothBluetooth.Listener {

    private TMBluetooth bluetooth;
    private Device connectedDevice = null;

    public void setBluetooth(TMBluetooth bluetooth) {
        this.bluetooth = bluetooth;
    }


    @Override
    public void onBluetoothNotSupported() {
        // Too bad
    }

    @Override
    public void onBluetoothNotEnabled() {
        /*assert mBluetoothFragment != null;
        mBluetoothFragment.promptEnableBluetooth();*/
    }

    @Override
    public void onConnecting(Device device) {
        Log.d("onConnection",device.getName());
    }

    @Override
    public void onConnected(Device device) {
        Log.d("onConnected",device.getName());
        this.connectedDevice = device;
        this.bluetooth.setConnectedDevice(device);
        BluetoothIconReceiver.sendStatusOk(String.format(TMApplication.getContext().getResources().getString(R.string.connected_to), device.getName()));
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public void onDisconnected() {
        Log.d("onDisco","disconnected");
        this.connectedDevice = null;
        this.bluetooth.setConnectedDevice(null);
    }

    @Override
    public void onConnectionFailed(Device device) {
        if (device != null)
        {
            Log.d("onConFailed", device.getName());
            BluetoothIconReceiver.sendStatusError(String.format(TMApplication.getContext().getResources().getString(R.string.connection_failed_to),device.getName()));
        }
    }

    @Override
    public void onDiscoveryStarted() {
        Log.d("BT","onDiscoveryStarted");
    }

    @Override
    public void onDiscoveryFinished() {
        Log.d("BT", "onDiscoveryFinished");
    }

    @Override
    public void onNoDevicesFound() {
        Log.d("BT","No Devices Found");
    }

    //Discovery
    @Override
    public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {
        TMBluetoothManager.getInstance().showBluetoothDevicesDialog(deviceList, connectionCallback);
        //receives discovered devices list and connection callback
        //you can filter devices list and connect to specific one
        //connectionCallback.connectTo(deviceList.get(position));

    }

    @Override
    public void onDataReceived(int data) {
        this.bluetooth.onDataReceived(data);
    }

    public Device getConnectedDevice() {
        return connectedDevice;
    }
}
