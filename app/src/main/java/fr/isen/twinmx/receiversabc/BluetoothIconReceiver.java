package fr.isen.twinmx.receiversabc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

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
    private final TMBluetooth bluetooth;


    public BluetoothIconReceiver(ImageView bluetoothIcon, ProgressBar progressBar, View container, final TMBluetooth bluetooth) {
        this.bluetoothIcon = bluetoothIcon;
        this.progressBar = progressBar;
        this.container = container;
        this.bluetooth = bluetooth;
        this.updateIcon();
    }

    private void updateIcon() {
        if (!bluetooth.isBluetoothEnabled()) {
            this.updateIcon(R.drawable.ic_bluetooth_disabled_white_24dp, R.drawable.circular_image_view_grey);
        } else if (!bluetooth.isConnected()) {
            this.updateIcon(R.drawable.ic_bluetooth_white_24dp, R.drawable.circular_image_view);
        } else {
            this.updateIcon(R.drawable.ic_bluetooth_connected_white_24dp, R.drawable.circular_image_view_green);
        }

    }

    private void updateIcon(String status) {
        if (status.equals(EXTRA_STATUS_OK)) {
            this.connected(null);
        } else if (status.equals(EXTRA_STATUS_CONNECTING)) {
            this.connecting();
        } else if (status.equals(EXTRA_STATUS_ENABLED)) {
            this.enabled();
        } else if (status.equals(EXTRA_STATUS_ERROR)) {
            this.errorOrDisabled();
        }
    }

    public void connected(String message) {
        this.showLoading(false);
        this.updateIcon(R.drawable.ic_bluetooth_connected_white_24dp, R.drawable.circular_image_view_green);
        inform(message);
    }

    public void connecting() {
        this.showLoading(true);
    }

    public void disconnected() {
        enabled();
    }

    public void enabled() {
        this.showLoading(false);
        this.updateIcon(R.drawable.ic_bluetooth_white_24dp, R.drawable.circular_image_view);
    }

    public void errorOrDisabled() {
        if (!bluetooth.isBluetoothEnabled()) {
            this.disabled();
        } else {
            this.error(TMApplication.loadString(R.string.connection_failed));
        }
    }

    public void disabled() {
        this.showLoading(false);
        this.updateIcon(R.drawable.ic_bluetooth_disabled_white_24dp, R.drawable.circular_image_view_grey);
    }

    public void error(String message) {
        this.showLoading(false);
        this.updateIcon(R.drawable.ic_bluetooth_white_24dp, R.drawable.circular_image_view_red);
        inform(message);
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
        } else if (intent != null && intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            this.updateIcon();
        }
    }

    public void register(Activity activity) {
        activity.registerReceiver(this, new IntentFilter(BluetoothIconReceiver.ACTION));
        activity.registerReceiver(this, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
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
        sendStatusOk(message, TMApplication.getContext());
    }

    public static void sendStatusOk(final String message, Context context) {
        sendStatus(context, EXTRA_STATUS_OK, message);
    }

    public static void sendStatusError(final String message) {
        sendStatusError(message, TMApplication.getContext());
    }

    public static void sendStatusError(final String message, Context context) {
        sendStatus(context, EXTRA_STATUS_ERROR, message);
    }

    public static void sendStatusEnabled() {
        sendStatusEnabled(TMApplication.getContext());
    }

    public static void sendStatusEnabled(Context context) {
        sendStatus(context, EXTRA_STATUS_ENABLED, null);
    }

    public static void sendStatusConnecting() {
        sendStatusConnecting(TMApplication.getContext());
    }

    public static void sendStatusConnecting(Context context) {
        sendStatus(context, EXTRA_STATUS_CONNECTING, null);
    }


}
