package fr.isen.twinmx.utils.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import fr.isen.twinmx.R;
import fr.isen.twinmx.async.FileInfiniteReaderAsyncTask;
import fr.isen.twinmx.listeners.OnChangeInputListener;
import fr.isen.twinmx.model.TMFile;
import fr.isen.twinmx.model.TMInput;
import fr.isen.twinmx.receivers.BluetoothIconReceiver;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.RealmDeviceRepository;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.model.RealmDevice;
import fr.isen.twinmx.utils.bluetooth.SmoothBluetoothFork.TMBluetoothService;
import fr.isen.twinmx.utils.bluetooth.SmoothBluetoothFork.TMDevice;
import fr.isen.twinmx.utils.bluetooth.SmoothBluetoothFork.TMSmoothBluetooth;
import fr.isen.twinmx.utils.TMBluetoothDialogAdapter;

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
    private Activity activity;
    private BluetoothIconReceiver bluetoothIconReceiver;
    private MaterialDialog bluetoothDevicesDialog;
    private TMFile connectedFile;

    private List<OnChangeInputListener> onChangeInputListeners = new LinkedList<>();


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
        this.showFilesDialog();
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

        this.notifyConnect();

        //BluetoothIconReceiver.sendStatusOk(String.format(TMApplication.getActivity().getResources().getString(R.string.connected_to), device.getName()));
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public void onDisconnected() {
        Log.d("TMBluetooth", "onDisconnected ["+this.getConnectedDevice()+"]");
        if (this.bluetoothIconReceiver != null) {
            this.bluetoothIconReceiver.disconnected();
        }
        this.notifyDisconnect();
    }

    @Override
    public void onConnectionFailed(TMDevice device) {
        Log.d("TMBluetooth", "onConnectionFailed ["+device+"]");
        //BluetoothIconReceiver.sendStatusError(String.format(TMApplication.getActivity().getResources().getString(R.string.connection_failed_to),device.getName()));
        if (this.bluetoothIconReceiver != null) {
            this.bluetoothIconReceiver.error(TMApplication.loadString(R.string.connection_failed_to, device != null ? device.getName() : ""));
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
        this.showBluetoothDevicesDialog(deviceList, connectionCallback, true);
    }

    private void showFilesDialog() {
        showBluetoothDevicesDialog(new ArrayList<TMDevice>(), null, true);
    }

    private void showBluetoothDevicesDialog(List<TMDevice> devices, ConnectionCallback connectionCallback, boolean isShowFiles) {
        List<TMInput> inputs = new LinkedList<>();
        if (this.isBluetoothEnabled()) {
            for(TMDevice d : devices) {
                inputs.add(d);
            }
        }


        if (isShowFiles) {
            for(TMFile f : getFilesFromDocumentDirectory()) {
                inputs.add(f);
            }
        }

        if (this.bluetoothDevicesDialog == null || !this.bluetoothDevicesDialog.isShowing()) {

            this.bluetoothDevicesDialog = new MaterialDialog.Builder(this.activity)
                    .title(TMApplication.getContext().getResources().getString(R.string.select_bt_device))
                    .adapter(new TMBluetoothDialogAdapter(inputs, connectionCallback, this), null)
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

    public void setActivity(Activity activity)
    {
        hideBluetoothDevicesDialog();
        this.activity = activity;
    }

    private FileInfiniteReaderAsyncTask fileInfiniteReaderAsyncTask;

    public void readFromFileIndefinitely(TMFile file) {
        this.connectedFile = file;
        if (this.connectedFile == null) return;

        if (fileInfiniteReaderAsyncTask == null || fileInfiniteReaderAsyncTask.isStopped()) {
            fileInfiniteReaderAsyncTask = new FileInfiniteReaderAsyncTask(this, connectedFile);
        }
        else {
            fileInfiniteReaderAsyncTask.stopAndWait();
            fileInfiniteReaderAsyncTask = new FileInfiniteReaderAsyncTask(this, connectedFile);
        }

        if (Build.VERSION.SDK_INT >= 11)
            fileInfiniteReaderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            fileInfiniteReaderAsyncTask.execute();
        if (this.bluetoothIconReceiver != null) {
            this.bluetoothIconReceiver.fileConnected(null);
        }

        this.notifyConnect();
        this.setChanged();
        this.notifyObservers();
    }

    public void stopReadingFromFile() {
        this.connectedFile = null;
        if (fileInfiniteReaderAsyncTask != null) {
            fileInfiniteReaderAsyncTask.stop();
        }
        if (this.bluetoothIconReceiver != null) {
            this.bluetoothIconReceiver.fileDisconnected(this.isBluetoothEnabled());
        }
        fileInfiniteReaderAsyncTask = null;
        this.notifyDisconnect();
    }

    public Activity getActivity() {
        return activity;
    }

    public boolean hasConnectedFile() {
        return connectedFile != null;
    }

    public TMFile getConnectedFile() {
        return connectedFile;
    }

    private List<TMFile> getFilesFromDocumentDirectory() {
        try {
            String[] fileNames = this.getActivity().getResources().getAssets().list("measures");

            List<TMFile> result = new LinkedList<>();
            for(String fn : fileNames) {
                result.add(new TMFile(fn,fn,false));
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private File getDocumentDirectory(Context context) throws IOException {

        // Get the directory for the app's private pictures directory.
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "data");
        if (!directory.mkdirs()) {
            Log.e("WRITE", "Directory not created");
        }
        return directory;
    }

    public void showDialog() {
        if (this.isBluetoothEnabled()) {
            this.scanDevices();
        }
        else {
            this.showFilesDialog();
        }
    }


    public void addOnChangeInputListener(OnChangeInputListener onChangeInputListener) {
        if (!onChangeInputListeners.contains(onChangeInputListener)) {
            onChangeInputListeners.add(onChangeInputListener);
        }
    }

    private void notifyConnect() {
        for(OnChangeInputListener l : onChangeInputListeners) {
            l.onConnect();
        }
    }

    private void notifyDisconnect() {
        for(OnChangeInputListener l : onChangeInputListeners) {
            l.onDisconnect();
        }
    }

    public void removeListeners() {
        onChangeInputListeners.clear();
    }
}
