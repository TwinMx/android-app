package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;

import java.util.Observable;
import java.util.Observer;

import fr.isen.twinmx.util.Bluetooth.TMBluetooth;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;
import fr.isen.twinmx.util.TMSnackBar;

/**
 * Created by Clement on 19/01/2017.
 */

public abstract class BluetoothFragment extends Fragment implements Observer {

    private TMBluetoothManager tmBluetoothManager;
    private TMBluetooth tmBluetooth;

    private static boolean isFirstInit = true;

    public abstract CoordinatorLayout getCoordinatorLayout();

    private void initObserver() {
        if (tmBluetoothManager == null || tmBluetooth == null) {
            tmBluetoothManager = TMBluetoothManager.getInstance();
            if (tmBluetooth == null) tmBluetooth = tmBluetoothManager.getBluetooth();
            tmBluetoothManager.getDataManager().addObserver(this);
        }
    }

    private void checkBluetothState() {
        if (isFirstInit) {
            if (!tmBluetoothManager.isBluetoothEnabled()) {
                showEnabledBluetoothSnackBar(); //Prompt to enable bluetooth. Once bluetooth is enabled, displays list of devices
            } else {
                if (!tmBluetoothManager.isBluetoothConnectedToDevice()) {
                    tmBluetoothManager.connectToKnownDevicesOrScanDevices();
                }
            }
        }
    }

    public void showEnabledBluetoothSnackBar() {
        final CoordinatorLayout coordinatorLayout = getCoordinatorLayout();
        if (coordinatorLayout != null) {
            (TMSnackBar.makeBluetooth(this.getActivity().getApplicationContext(), coordinatorLayout,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TMBluetoothManager.getInstance().enableBluetooth();
                        }
                    }))
                    .show();
        }
    }

    public void onResume() {
        super.onResume();
        initObserver();
        checkBluetothState();
        BluetoothFragment.isFirstInit = false;
    }

    @Override
    public void update(Observable observable, Object o) {
        int size = this.tmBluetoothManager.getDataManager().getData().size();
        Log.d("Observer", "size: "+size);
    }

}
