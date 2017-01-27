package fr.isen.twinmx.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.isen.twinmx.R;
import fr.isen.twinmx.Receivers.BluetoothIconReceiver;
import fr.isen.twinmx.util.Bluetooth.SmoothBluetoothFork.TMDevice;
import fr.isen.twinmx.util.Bluetooth.SmoothBluetoothFork.TMSmoothBluetooth;
import fr.isen.twinmx.util.Bluetooth.TMBluetooth;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 9/27/2016.
 */
public class TMBluetoothDialogAdapter extends RecyclerView.Adapter<TMDeviceHolder> {

    private final TMSmoothBluetooth.ConnectionCallback connectionCallback;
    private final TMBluetooth mBluetooth;
    private List<TMDevice> mDevices;

    public TMBluetoothDialogAdapter(List<TMDevice> devices, TMSmoothBluetooth.ConnectionCallback connectionCallback, TMBluetooth bluetooth) {
        this.mDevices = devices;
        this.connectionCallback = connectionCallback;
        this.mBluetooth = bluetooth;
    }

    @Override
    public TMDeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_device_item, parent, false);
        final TMDeviceHolder holder = new TMDeviceHolder(view, mBluetooth);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothIconReceiver.sendStatusConnecting();
                mBluetooth.hideBluetoothDevicesDialog();
                connectionCallback.connectTo(holder.getDevice());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(TMDeviceHolder holder, int position) {
        holder.bind(this.mDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return this.mDevices.size();
    }
}
