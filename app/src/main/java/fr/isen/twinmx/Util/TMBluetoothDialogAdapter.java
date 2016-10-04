package fr.isen.twinmx.Util;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.isen.twinmx.R;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 9/27/2016.
 */
public class TMBluetoothDialogAdapter extends RecyclerView.Adapter<TMDeviceHolder> {

    private final SmoothBluetooth.ConnectionCallback connectionCallback;
    private List<Device> mDevices;

    public TMBluetoothDialogAdapter(List<Device> devices, SmoothBluetooth.ConnectionCallback connectionCallback) {
        this.mDevices = devices;
        this.connectionCallback = connectionCallback;
    }

    @Override
    public TMDeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_device_item, parent, false);
        final TMDeviceHolder holder = new TMDeviceHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BTClick",holder.getName());
                connectionCallback.connectTo(holder.getDevice());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(TMDeviceHolder holder, int position) {
        holder.bind(mDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }
}
