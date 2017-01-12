package fr.isen.twinmx.Receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.util.Bluetooth.TMBluetooth;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;

/**
 * Created by cdupl on 10/5/2016.
 */

public class BluetoothIconReceiver extends BroadcastReceiver {

    public static final String ACTION = "fr.isen.twinmx.bluetooth";
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_STATUS_OK = "status_ok";
    public static final String EXTRA_STATUS_ERROR = "status_error";
    private static final String EXTRA_STATUS_CONNECTING = "status_connecting";
    public static final String EXTRA_STATUS_ENABLED = "status_enabled";
    public static final String EXTRA_MESSAGE = "message";


    private final ImageView bluetoothIcon;
    private final View container;
    private final ProgressBar progressBar;


    public BluetoothIconReceiver(ImageView bluetoothIcon, ProgressBar progressBar, View container) {
        this.bluetoothIcon = bluetoothIcon;
        this.progressBar = progressBar;
        this.bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TMBluetooth bt = TMBluetoothManager.getInstance().getBluetooth();
                if (bt.isBluetoothEnabled()) {
                    bt.tryConnection();
                }
                else {
                    TMBluetoothManager.getInstance().enableBluetooth();
                }
            }
        });
        this.container = container;
        this.updateIcon();
    }

    private void updateIcon() {
        final TMBluetooth bt = TMBluetoothManager.getInstance().getBluetooth();
        if (!bt.isBluetoothEnabled()) {
            this.updateIcon(R.drawable.ic_bluetooth_disabled_white_24dp, R.drawable.circular_image_view_grey);
        }
        else if (!bt.isConnected()) {
            this.updateIcon(R.drawable.ic_bluetooth_white_24dp, R.drawable.circular_image_view);
        }
        else {
            this.updateIcon(R.drawable.ic_bluetooth_connected_white_24dp, R.drawable.circular_image_view_green);
        }

    }

    private void updateIcon(String status) {
        if (status.equals(EXTRA_STATUS_OK)) {
            this.showLoading(false);
            this.updateIcon(R.drawable.ic_bluetooth_connected_white_24dp, R.drawable.circular_image_view_green);
        }
        else if (status.equals(EXTRA_STATUS_CONNECTING)) {
            this.showLoading(true);
        }
        else if (status.equals(EXTRA_STATUS_ENABLED)) {
            this.showLoading(false);
            this.updateIcon(R.drawable.ic_bluetooth_white_24dp, R.drawable.circular_image_view);
        }
        else if (status.equals(EXTRA_STATUS_ERROR)) {
            this.showLoading(false);
            final TMBluetooth bt = TMBluetoothManager.getInstance().getBluetooth();
            if (!bt.isBluetoothEnabled()) {
                this.updateIcon(R.drawable.ic_bluetooth_disabled_white_24dp, R.drawable.circular_image_view_grey);
            }
            else {
                this.updateIcon(R.drawable.ic_bluetooth_white_24dp, R.drawable.circular_image_view_red);
            }
        }
    }

    private void showLoading(boolean val) {
        this.bluetoothIcon.setVisibility(val ? View.GONE : View.VISIBLE);
        this.progressBar.setVisibility(val ? View.VISIBLE : View.GONE);
    }

    private void updateIcon(int image, int background) {
        if (this.bluetoothIcon != null) {
            this.bluetoothIcon.setImageResource(image);
            this.bluetoothIcon.setBackgroundResource(background);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(ACTION)) {
            this.updateIcon(intent.getStringExtra(EXTRA_STATUS));
            this.inform(intent.getStringExtra(EXTRA_MESSAGE));
        }
        else if (intent != null && intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            this.updateIcon();
        }
    }

    private void inform(final String message) {
        if (container != null && message != null && !message.isEmpty()) {
            Snackbar.make(container, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private static void sendStatus(Context context, final String statusCode, final String message) {
        final Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_STATUS, statusCode);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    public static void sendStatusOk(final String message) {
        sendStatus(TMApplication.getContext(), EXTRA_STATUS_OK, message);
    }

    public static void sendStatusError(final String message) {
        sendStatus(TMApplication.getContext(), EXTRA_STATUS_ERROR, message);
    }

    public static void sendStatusEnabled() {
        sendStatus(TMApplication.getContext(), EXTRA_STATUS_ENABLED, null);


    }

    public static void sendStatusConnecting() {
        sendStatus(TMApplication.getContext(), EXTRA_STATUS_CONNECTING, null);
    }
}