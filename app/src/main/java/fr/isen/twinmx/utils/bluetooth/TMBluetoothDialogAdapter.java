package fr.isen.twinmx.utils.bluetooth;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.isen.twinmx.model.TMFile;
import fr.isen.twinmx.model.TMInput;
import fr.isen.twinmx.receivers.BluetoothIconReceiver;
import fr.isen.twinmx.utils.TMDeviceHolder;
import fr.isen.twinmx.utils.bluetooth.SmoothBluetoothFork.TMDevice;
import fr.isen.twinmx.utils.bluetooth.SmoothBluetoothFork.TMSmoothBluetooth;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

/**
 * Created by cdupl on 9/27/2016.
 */
public class TMBluetoothDialogAdapter extends RecyclerView.Adapter<TMDeviceHolder> {

    private final TMSmoothBluetooth.ConnectionCallback connectionCallback;
    private final TMBluetooth mBluetooth;
    private List<TMInput> mInputs;

    public TMBluetoothDialogAdapter(List<TMInput> inputs, TMSmoothBluetooth.ConnectionCallback connectionCallback, TMBluetooth bluetooth) {
        this.mInputs = inputs;
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
                TMDevice device = holder.getDevice();
                TMFile file = holder.getFile();

                mBluetooth.hideBluetoothDevicesDialog();

                if (device != null) {
                    BluetoothIconReceiver.sendStatusConnecting();
                    connectionCallback.connectTo(device);
                }
                else if (file != null) {
                    mBluetooth.readFromFileIndefinitely(file);
                }

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(TMDeviceHolder holder, int position) {
        TMInput input = this.mInputs.get(position);
        if (input instanceof TMDevice) {
            holder.bind((TMDevice)input);
        }
        else {
            holder.bind((TMFile)input);
        }
    }

    @Override
    public int getItemCount() {
        return this.mInputs.size();
    }
}
