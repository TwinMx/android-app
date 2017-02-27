package fr.isen.twinmx.utils;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.model.TMFile;
import fr.isen.twinmx.model.TMInput;
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
    private TMFile file;

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
                    if (getConnectedDevice() != null) {
                        bluetooth.disconnect();
                        disconnectIcon.setVisibility(View.GONE);
                        updateIcon();
                    }
                    else if (getConnectedFile() != null && getConnectedFile().equals(file)) {
                        bluetooth.stopReadingFromFile();
                        disconnectIcon.setVisibility(View.GONE);
                        updateIcon();
                    }

                }
            }
        });
        this.bluetooth = bluetooth;
    }

    public void bind(TMDevice device) {
        this.device = device;
        this.file = null;

        final TMDevice connectedDevice = this.getConnectedDevice();
        this.name.setText(device.getName());
        this.mac.setText(device.getAddress());
        this.updateIcon();
        if (device != null && connectedDevice != null) {
            if (device.getAddress().equals(connectedDevice.getAddress())) {
                this.disconnectIcon.setVisibility(View.VISIBLE);
            }
            else {
                this.disconnectIcon.setVisibility(View.GONE);
            }
        }
        else {
            this.disconnectIcon.setVisibility(View.GONE);
        }

    }

    public void bind(TMFile file) {
        this.device = null;
        this.file = file;

        final TMFile connectedFile = this.getConnectedFile();

        this.name.setText(file.getFileName());
        this.mac.setText("");
        this.updateIcon();
        if (file != null && connectedFile != null) {
            if (file.getFileName().equals(connectedFile.getFileName())) {
                this.disconnectIcon.setVisibility((View.VISIBLE));
            }
            else {
                this.disconnectIcon.setVisibility(View.GONE);
            }
        }
        else {
            this.disconnectIcon.setVisibility(View.GONE);
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
        final TMFile connectedFile = this.getConnectedFile();

        if (file != null) { //FILE
            if (connectedFile != null && file.getFileName().equals(connectedFile.getFileName())) { //Connected file
                setConnectedFileImage();
            }
            else {
                setDisconnectedFileImage();
            }
        }
        else if (device != null) { //DEVICE
            if (connectedDevice != null && device.getAddress().equals(connectedDevice.getAddress())) { //Connected device
                setConnectedDeviceImage();
            }
            else {
                if (device.isTwinMax()) {
                    setDisconnectedTwinMxImage();
                }
                else {
                    setDisconnectedDeviceImage();
                }
            }
        }
    }

    private void setDisconnectedFileImage() {
        this.bluetoothIcon.setImageResource(R.drawable.ic_insert_drive_file_white_24dp);
        this.bluetoothIcon.setBackgroundResource(R.color.transparent);
        this.bluetoothIcon.setColorFilter(ContextCompat.getColor(TMApplication.getContext(),R.color.grey500));
        this.resetPadding();
    }

    private void setConnectedFileImage() {
        this.bluetoothIcon.setImageResource(R.drawable.ic_insert_drive_file_white_24dp);
        this.bluetoothIcon.setBackgroundResource(R.color.transparent);
        this.bluetoothIcon.setColorFilter(ContextCompat.getColor(TMApplication.getContext(),R.color.green500));
        this.resetPadding();
    }

    private void setDisconnectedTwinMxImage() {
        this.bluetoothIcon.setImageResource(R.mipmap.ic_bluetooth_twinmx);
        this.bluetoothIcon.setBackgroundResource(R.color.transparent);
        this.bluetoothIcon.setPadding(0,0,0,0);
    }

    private void setConnectedDeviceImage() {
        this.bluetoothIcon.setBackgroundResource(R.drawable.circular_image_view_green);
        this.bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_connected_white_24dp);
        this.bluetoothIcon.setColorFilter(ContextCompat.getColor(TMApplication.getContext(),R.color.white));
        this.resetPadding();
    }

    private void resetPadding() {
        int pixelsPadding = getDpAsPixels(5);
        this.bluetoothIcon.setPadding(pixelsPadding, pixelsPadding, pixelsPadding, pixelsPadding);
    }

    private void setDisconnectedDeviceImage() {
        this.bluetoothIcon.setBackgroundResource(R.drawable.circular_image_view);
        this.bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_white_24dp);
        this.bluetoothIcon.setColorFilter(ContextCompat.getColor(TMApplication.getContext(),R.color.white));
        int pixelsPadding = getDpAsPixels(5);
        this.bluetoothIcon.setPadding(pixelsPadding, pixelsPadding, pixelsPadding, pixelsPadding);
    }

    private int getDpAsPixels(int sizeInDp) {
        float scale = TMApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (sizeInDp*scale + 0.5f);
    }

    public TMInput getInput() {
        if (this.device != null) {
            return this.device;
        }
        else if (this.file != null) {
            return this.file;
        }
        return null;
    }

    public TMFile getConnectedFile() {
        return bluetooth.getConnectedFile();
    }

    public TMFile getFile() {
        return file;
    }
}
