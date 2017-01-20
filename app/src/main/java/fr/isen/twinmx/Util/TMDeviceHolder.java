package fr.isen.twinmx.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.isen.twinmx.R;
import fr.isen.twinmx.util.Bluetooth.TMBluetooth;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;
import io.palaima.smoothbluetooth.Device;

/**
 * Created by cdupl on 9/27/2016.
 */
public class TMDeviceHolder extends RecyclerView.ViewHolder {

    private static TMBluetooth bluetooth;

    private final ImageView bluetoothIcon;
    private Device device;
    private TextView name;
    private TextView mac;
    private ImageView disconnectIcon;

    public TMDeviceHolder(View itemView) {
        super(itemView);
        this.bluetoothIcon = (ImageView) itemView.findViewById(R.id.recycler_view_bluetooth_icon);
        this.name = (TextView) itemView.findViewById(R.id.recycler_view_device_item_name);
        this.mac = (TextView) itemView.findViewById(R.id.recycler_view_device_item_mac);
        this.disconnectIcon = (ImageView) itemView.findViewById(R.id.recycler_view_disconnect_icon);
        this.disconnectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disconnectIcon.getVisibility() == View.VISIBLE) {
                    TMBluetoothManager.getInstance().getBluetooth().disconnect();
                    disconnectIcon.setVisibility(View.GONE);
                    updateIcon();
                }
            }
        });
    }

    public void bind(Device device) {
        this.device = device;
        final Device connectedDevice = this.getConnectedDevice();
        this.name.setText(device.getName());
        this.mac.setText(device.getAddress());

        if (this.device != null && connectedDevice != null) {
            if (this.device.getAddress().equals(connectedDevice.getAddress())) {
                this.disconnectIcon.setVisibility(View.VISIBLE);
            }
            else {
                this.disconnectIcon.setVisibility(View.GONE);
            }
        }

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

    private Device getConnectedDevice() {
        if (bluetooth == null) bluetooth = TMBluetoothManager.getInstance().getBluetooth();
        return bluetooth.getConnectedDevice();
    }

    private void updateIcon() {
        final Device connectedDevice = this.getConnectedDevice();
        if (connectedDevice != null && device != null && device.getAddress().equals(connectedDevice.getAddress())) {
            this.bluetoothIcon.setBackgroundResource(R.drawable.circular_image_view_green);
            this.bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_connected_white_24dp);
        }
        else {
            this.bluetoothIcon.setBackgroundResource(R.drawable.circular_image_view);
            this.bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_white_24dp);
        }
    }
}
