package fr.isen.twinmx.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.utils.bluetooth.SmoothBluetoothFork.TMDevice;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

/**
 * Created by cdupl on 9/27/2016.
 */
public class TMDeviceHolder extends RecyclerView.ViewHolder {

    public static final int LAYOUT = R.layout.recycler_view_device_item;
    private TMBluetooth bluetooth;

    private final ImageView bluetoothIcon;
    private TMDevice device;
    private TextView name;
    private TextView mac;
    private ImageView disconnectIcon;

    public TMDeviceHolder(View itemView, final TMBluetooth bluetooth) {
        super(itemView);
        this.bluetoothIcon = (ImageView) itemView.findViewById(R.id.recycler_view_bluetooth_icon);
        this.name = (TextView) itemView.findViewById(R.id.recycler_view_device_item_name);
        this.mac = (TextView) itemView.findViewById(R.id.recycler_view_device_item_mac);
        this.disconnectIcon = (ImageView) itemView.findViewById(R.id.recycler_view_disconnect_icon);
        this.disconnectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disconnectIcon.getVisibility() == View.VISIBLE) {
                    bluetooth.disconnect();
                    disconnectIcon.setVisibility(View.GONE);
                    updateIcon();
                }
            }
        });
        this.bluetooth = bluetooth;
    }

    public void bind(TMDevice device) {
        this.device = device;
        final TMDevice connectedDevice = this.getConnectedDevice();
        this.name.setText(device.getName());
        this.mac.setText(device.getAddress());
        this.updateIcon();
        if (this.device != null && connectedDevice != null) {
            if (this.device.getAddress().equals(connectedDevice.getAddress())) {
                this.disconnectIcon.setVisibility(View.VISIBLE);
            }
            else {
                this.disconnectIcon.setVisibility(View.GONE);
            }
        }

    }

    public TMDevice getDevice() {
        return this.device;
    }

    public String getName() {
        return this.name.getText().toString();
    }

    public String getMac() {
        return mac.getText().toString();
    }

    private TMDevice getConnectedDevice() {
        return bluetooth.getConnectedDevice();
    }

    private void updateIcon() {
        final TMDevice connectedDevice = this.getConnectedDevice();
        if (connectedDevice != null && device != null && device.getAddress().equals(connectedDevice.getAddress())) {
            this.bluetoothIcon.setBackgroundResource(R.drawable.circular_image_view_green);
            this.bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_connected_white_24dp);
            int pixelsPadding = getDpAsPixels(5);
            this.bluetoothIcon.setPadding(pixelsPadding, pixelsPadding, pixelsPadding, pixelsPadding);
        }
        else {
            setTwinMaxImage(device.isTwinMax());
        }
    }

    private void setTwinMaxImage(boolean isTwinMax) {
        if (isTwinMax) {
            this.bluetoothIcon.setImageResource(R.mipmap.ic_bluetooth_twinmx);
            this.bluetoothIcon.setBackgroundResource(R.color.transparent);
            this.bluetoothIcon.setPadding(0,0,0,0);
        } else {
            this.bluetoothIcon.setBackgroundResource(R.drawable.circular_image_view);
            this.bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_white_24dp);
            int pixelsPadding = getDpAsPixels(5);
            this.bluetoothIcon.setPadding(pixelsPadding, pixelsPadding, pixelsPadding, pixelsPadding);
        }
    }

    private int getDpAsPixels(int sizeInDp) {
        float scale = TMApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (sizeInDp*scale + 0.5f);
    }
}
