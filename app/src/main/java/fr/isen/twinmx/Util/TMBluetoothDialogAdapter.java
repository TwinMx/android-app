package fr.isen.twinmx.Util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.isen.twinmx.R;
import io.palaima.smoothbluetooth.Device;

/**
 * Created by cdupl on 9/27/2016.
 */
public class TMBluetoothDialogAdapter extends RecyclerView.Adapter<TMDeviceHolder> {

    private List<Device> mDevices;

    public TMBluetoothDialogAdapter(List<Device> devices) {
        mDevices = devices;
    }

    @Override
    public TMDeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_device_item, parent, false);
        return new TMDeviceHolder(view);
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
