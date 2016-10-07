package fr.isen.twinmx.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fr.isen.twinmx.R;
import io.palaima.smoothbluetooth.Device;

/**
 * Created by cdupl on 9/27/2016.
 */
public class TMDeviceHolder extends RecyclerView.ViewHolder {

    private Device device;
    private TextView name;
    private TextView mac;

    public TMDeviceHolder(View itemView) {
        super(itemView);
        this.name = (TextView) itemView.findViewById(R.id.recycler_view_device_item_name);
        this.mac = (TextView) itemView.findViewById(R.id.recycler_view_device_item_mac);
    }

    public void bind(Device device) {
        this.device = device;
        this.name.setText(device.getName());
        this.mac.setText(device.getAddress());
    }

    public Device getDevice() {
        return this.device;
    }

    public String getName() {
        return this.name.getText().toString();
    }

    public String getMac() {
        return mac.getText().toString();
    }
}
