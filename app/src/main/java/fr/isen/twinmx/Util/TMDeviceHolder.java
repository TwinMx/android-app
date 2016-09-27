package fr.isen.twinmx.Util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fr.isen.twinmx.R;
import io.palaima.smoothbluetooth.Device;

/**
 * Created by cdupl on 9/27/2016.
 */
public class TMDeviceHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView mac;

    public TMDeviceHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.recycler_view_device_item_name);
        mac = (TextView) itemView.findViewById(R.id.recycler_view_device_item_mac);
    }

    public void bind(Device device) {
        name.setText(device.getName());
        mac.setText(device.getAddress());
    }
}
