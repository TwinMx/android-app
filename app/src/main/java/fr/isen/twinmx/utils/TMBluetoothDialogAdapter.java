package fr.isen.twinmx.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.isen.twinmx.receivers.BluetoothIconReceiver;
import fr.isen.twinmx.utils.bluetooth.SmoothBluetoothFork.TMDevice;
import fr.isen.twinmx.utils.bluetooth.SmoothBluetoothFork.TMSmoothBluetooth;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

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
        final View view = LayoutInflater.from(parent.getContext()).inflate(TMDeviceHolder.LAYOUT, parent, false);
        final TMDeviceHolder holder = new TMDeviceHolder(view, mBluetooth);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*BluetoothIconReceiver.sendStatusConnecting();
                mBluetooth.hideBluetoothDevicesDialog();
                connectionCallback.connectTo(holder.getDevice());*/
                mBluetooth.hideBluetoothDevicesDialog();
                mBluetooth.readFromFileIndefinitely();
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
