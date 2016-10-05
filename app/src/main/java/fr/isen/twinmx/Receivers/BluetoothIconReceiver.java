package fr.isen.twinmx.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;

/**
 * Created by cdupl on 10/5/2016.
 */

public class BluetoothIconReceiver extends BroadcastReceiver {

    public static final String ACTION = "fr.isen.twinmx.bluetooth";
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_STATUS_OK = "status_ok";
    public static final String EXTRA_STATUS_ERROR = "status_error";
    public static final String EXTRA_MESSAGE = "message";


    private final ImageView bluetoothIcon;
    private final View container;


    public BluetoothIconReceiver(ImageView bluetoothIcon, View container) {
        this.bluetoothIcon = bluetoothIcon;
        this.container = container;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            String status = intent.getStringExtra(EXTRA_STATUS);
            if (status.equals(EXTRA_STATUS_OK)) {
                assert bluetoothIcon != null;
                bluetoothIcon.setBackground(TMApplication.getContext().getResources().getDrawable(R.drawable.circular_image_view_green));
            }
            else {
                assert bluetoothIcon != null;
                bluetoothIcon.setBackground(TMApplication.getContext().getResources().getDrawable(R.drawable.circular_image_view_red));
            }

            String message = intent.getStringExtra(EXTRA_MESSAGE);
            if (message != null && !message.isEmpty()) {
                inform(message);
            }
        }
    }

    private void inform(String message) {
        assert container != null;
        Snackbar snack = Snackbar.make(container, message, Snackbar.LENGTH_SHORT);
        snack.show();

    }

    private static void sendStatus(Context context, String statusCode, String message) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_STATUS, statusCode);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    public static void sendStatusOk(String message) {
        sendStatus(TMApplication.getContext(), EXTRA_STATUS_OK, message);
    }

    public static void sendStatusError(String message) {
        sendStatus(TMApplication.getContext(), EXTRA_STATUS_ERROR, message);

    }
}
