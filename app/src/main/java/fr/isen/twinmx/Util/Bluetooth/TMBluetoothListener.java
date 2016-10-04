package fr.isen.twinmx.Util.Bluetooth;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.isen.twinmx.Fragments.BluetoothFragment;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 9/27/2016.
 */

public class TMBluetoothListener implements SmoothBluetooth.Listener {

    private BluetoothFragment mBluetoothFragment;

    public TMBluetoothListener(BluetoothFragment bluetoothFragment) {
        mBluetoothFragment = bluetoothFragment;
    }

    @Override
    public void onBluetoothNotSupported() {
        // Too bad
    }

    @Override
    public void onBluetoothNotEnabled() {
        mBluetoothFragment.promptEnableBluetooth();
    }

    @Override
    public void onConnecting(Device device) {
        Log.d("onConnection",device.getName());
    }

    @Override
    public void onConnected(Device device) {
        Log.d("onConnected",device.getName());
    }

    @Override
    public void onDisconnected() {
        Log.d("onDisco","disconnected");
    }

    @Override
    public void onConnectionFailed(Device device) {
        Log.d("onConFailed", device.getName());
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
        /*ArrayList<String> names = new ArrayList<>(deviceList.size());
        for(Device d : deviceList) {
            Log.d("onDeviceFound",d.getName());
            if (d.getName().startsWith("Twin")) {
                connectionCallback.connectTo(d);
            }
        }*/

        mBluetoothFragment.promptPairedBluetoothDevices(deviceList, connectionCallback);


        //receives discovered devices list and connection callback
        //you can filter devices list and connect to specific one
        //connectionCallback.connectTo(deviceList.get(position));

    }

    @Override
    public void onDataReceived(int data) {
        Log.d("Data", ((Integer)data).toString());
    }
}
