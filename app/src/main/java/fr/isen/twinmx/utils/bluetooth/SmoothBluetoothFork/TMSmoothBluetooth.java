package fr.isen.twinmx.utils.bluetooth.SmoothBluetoothFork;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import fr.isen.twinmx.receivers.BluetoothIconReceiver;

/**
 * Created by Clement on 26/01/2017.
 */

public abstract class TMSmoothBluetooth extends Observable {

    public Handler getmHandler() {
        return mHandler;
    }

    public void setTMBluetoothService(TMBluetoothService TMBluetoothService) {
        this.mBluetoothService = TMBluetoothService;
    }


    public enum Connection {
        SECURE,
        INSECURE
    }

    public enum ConnectionTo {
        ANDROID_DEVICE,
        OTHER_DEVICE
    }

    public interface ConnectionCallback {
        void connectTo(TMDevice device);
    }

    public interface Listener {
        void onBluetoothNotSupported();
        void onBluetoothNotEnabled();
        void onConnecting(TMDevice device);
        void onConnected(TMDevice device);
        void onDisconnected();
        void onConnectionFailed(TMDevice device);
        void onDiscoveryStarted();
        void onDiscoveryFinished();
        void onNoDevicesFound();
        void onDevicesFound(List<TMDevice> deviceList, TMSmoothBluetooth.ConnectionCallback connectionCallback);
        void onDataReceived(int data);
    }

    private static final String TAG = "BluetoothManager";

    private BluetoothAdapter mBluetoothAdapter;

    private TMBluetoothService mBluetoothService;

    private boolean isServiceRunning;

    private boolean mIsAndroid;

    private boolean mIsSecure;

    private boolean isConnected;

    private boolean isConnecting;

    private final Context mContext;

    private TMSmoothBluetooth.Listener mListener;

    private List<TMDevice> mDevices = new ArrayList<>();

    private TMDevice mCurrentDevice;

    public TMSmoothBluetooth(Context context) {
        this(context, TMSmoothBluetooth.ConnectionTo.OTHER_DEVICE, TMSmoothBluetooth.Connection.SECURE, null);
    }

    public TMSmoothBluetooth(Context context, TMSmoothBluetooth.Listener listener) {
        this(context, TMSmoothBluetooth.ConnectionTo.OTHER_DEVICE, TMSmoothBluetooth.Connection.SECURE, listener);
    }

    public TMSmoothBluetooth(Context context, TMSmoothBluetooth.ConnectionTo connectionTo, TMSmoothBluetooth.Connection connection,
                             TMSmoothBluetooth.Listener listener) {
        mContext = context;
        mListener = listener;
        mIsAndroid = connectionTo == TMSmoothBluetooth.ConnectionTo.ANDROID_DEVICE;
        mIsSecure = connection == TMSmoothBluetooth.Connection.SECURE;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private boolean checkBluetooth() {
        if (!isBluetoothAvailable()) {
            if (mListener != null) {
                mListener.onBluetoothNotSupported();
            }
            return false;
        }

        if (!isBluetoothEnabled()) {
            if (mListener != null) {
                mListener.onBluetoothNotEnabled();
            }
            return false;
        }

        if (this.isConnected()) {
            BluetoothIconReceiver.sendStatusOk(null);
        }
        else {
            BluetoothIconReceiver.sendStatusEnabled();
        }

        return true;
    }

    public void tryConnection() {
        if (!checkBluetooth()) {
            return;
        }

        mDevices.clear();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        String name = device.getName();
                        String address = device.getAddress();
                if(name != null && address != null) {
                    mDevices.add(new TMDevice(name, address, true));
                }
            }
        }

        Log.d(TAG, "Paired devices: " + mDevices.size());
        if (!mDevices.isEmpty()) {
            mListener.onDevicesFound(mDevices, new TMSmoothBluetooth.ConnectionCallback() {
                @Override
                public void connectTo(TMDevice device) {
                    if (device != null) {
                        connect(device, mIsAndroid, mIsSecure);
                    }
                }
            });
        } else {
            doDiscovery();
        }
    }

    public boolean isBluetoothAvailable() {
        try {
            if (mBluetoothAdapter == null || mBluetoothAdapter.getAddress().equals(null))
                return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isServiceAvailable() {
        return mBluetoothService != null;
    }

    public boolean startDiscovery() {
        return mBluetoothAdapter.startDiscovery();
    }

    public boolean isDiscovery() {
        return mBluetoothAdapter.isDiscovering();
    }

    public boolean cancelDiscovery() {
        return mBluetoothAdapter.cancelDiscovery();
    }

    public void setListener(TMSmoothBluetooth.Listener listener) {
        mListener = listener;
    }

    private void connect(TMDevice device, boolean android, boolean secure) {
        mCurrentDevice = device;
        if (mListener != null) {
            mListener.onConnecting(device);
        }
        connect(device.getAddress(), android, secure);
    }

    public TMDevice getConnectedDevice() {
        return mCurrentDevice;
    }

    public boolean isConnected() {
        return mCurrentDevice != null;
    }

    public void doDiscovery() {
        if (!checkBluetooth()) {
            return;
        }
        mCurrentDevice = null;
        mDevices.clear();
        if (mListener != null) {
            mListener.onDiscoveryStarted();
        }
        Log.d(TAG, "doDiscovery()");

        if (isDiscovery()) {
            mContext.unregisterReceiver(mReceiver);
            cancelDiscovery();
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);

        startDiscovery();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                Log.d(TAG, "Device found: " + device.getName() + " " + device.getAddress());
                if(!deviceExist(device)) {
                    mDevices.add(new TMDevice(device.getName(), device.getAddress(), false));
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "Discovery finished: " + mDevices.size());
                mContext.unregisterReceiver(mReceiver);
                if (mListener != null) {
                    mListener.onDiscoveryFinished();
                }
                connectAction(mDevices, mIsAndroid, mIsSecure);
            }
        }
    };

    private void connectAction(List<TMDevice> devices, final boolean android, final boolean secure) {
        if (mListener != null) {
            if (devices.isEmpty()) {
                mListener.onNoDevicesFound();
            } else {
                mListener.onDevicesFound(devices, new TMSmoothBluetooth.ConnectionCallback() {
                    @Override
                    public void connectTo(TMDevice device) {
                        if (device != null) {
                            connect(device, android, secure);
                        }
                    }
                });
            }
        }
    }

    private boolean deviceExist(BluetoothDevice device){
        for (TMDevice mDevice : mDevices) {
            if (mDevice.getAddress().contains(device.getAddress())) {
                return true;
            }
        }
        return false;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public abstract void setupService();

    private void startService(boolean isAndroid, boolean secure) {
        if (isServiceAvailable()) {
            if (mBluetoothService.getState() == TMBluetoothService.STATE_NONE) {
                isServiceRunning = true;
                mBluetoothService.start(isAndroid, secure);
            }
        }
    }

    public void stop() {
        mCurrentDevice = null;
        if (isServiceAvailable()) {
            isServiceRunning = false;
            mBluetoothService.stop();
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isServiceAvailable()) {
                    isServiceRunning = false;
                    mBluetoothService.stop();
                }
            }
        }, 500);
    }

    private void connect(String address, boolean android, boolean secure) {
        if (isConnecting) {
            return;
        }
        if (!isServiceAvailable()) {
            setupService();
        }
        startService(android, secure);
        if(BluetoothAdapter.checkBluetoothAddress(address)) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            mBluetoothService.connect(device);
        }
    }

    public void disconnect() {
        mCurrentDevice = null;
        if(isServiceAvailable()) {
            isServiceRunning = false;
            mBluetoothService.stop();
            if(mBluetoothService.getState() == TMBluetoothService.STATE_NONE) {
                isServiceRunning = true;
                mBluetoothService.start(mIsAndroid, mIsSecure);
            }
        }
    }

    public void send(String data) {
        send(data, false);
    }

    public void send(byte[] data) {
        send(data, false);
    }

    public void send(byte[] data, boolean CRLF) {
        if(isServiceAvailable() && mBluetoothService.getState() == TMBluetoothService.STATE_CONNECTED) {
            if(CRLF) {
                byte[] data2 = new byte[data.length + 2];
                System.arraycopy(data, 0, data2, 0, data.length);
                data2[data2.length] = 0x0A;
                data2[data2.length] = 0x0D;
                mBluetoothService.write(data2);
            } else {
                mBluetoothService.write(data);
            }
        }
    }

    public void send(String data, boolean CRLF) {
        if(isServiceAvailable() && mBluetoothService.getState() == TMBluetoothService.STATE_CONNECTED) {
            if(CRLF)
                data += "\r\n";
            mBluetoothService.write(data.getBytes());
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TMBluetoothService.MESSAGE_WRITE:
                    break;
                case TMBluetoothService.MESSAGE_READ:
                    int readBuf = (int) msg.obj;
                    //String readMessage = new String(readBuf);
                    //if(readBuf != null) {
                    if(mListener != null)
                        mListener.onDataReceived(readBuf);
                    // }
                    break;
                case TMBluetoothService.MESSAGE_DEVICE_NAME:
                    if(mListener != null) {
                        mListener.onConnected(mCurrentDevice);
                    }
                    isConnected = true;
                    break;
                case TMBluetoothService.MESSAGE_STATE_CHANGE:
                    /*if(mBluetoothStateListener != null)
                        mBluetoothStateListener.onServiceStateChanged(msg.arg1);*/
                    if(isConnected && msg.arg1 != TMBluetoothService.STATE_CONNECTED) {
                        isConnected = false;
                        if (mListener != null) {
                            mListener.onDisconnected();
                            mCurrentDevice = null;
                        }
                    }
                    if(!isConnecting && msg.arg1 == TMBluetoothService.STATE_CONNECTING) {
                        isConnecting = true;
                    } else if(isConnecting) {
                        isConnecting = false;
                        if(msg.arg1 != TMBluetoothService.STATE_CONNECTED) {
                            if (mListener != null) {
                                mListener.onConnectionFailed(mCurrentDevice);
                                mCurrentDevice = null;
                            }
                        }
                    }
                    break;
            }
        }
    };
}
