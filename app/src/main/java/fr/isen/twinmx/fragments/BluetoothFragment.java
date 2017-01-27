package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.data.Entry;
import com.mikepenz.materialize.color.Material;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import fr.isen.twinmx.model.MeasuresList;
import fr.isen.twinmx.util.Bluetooth.TMBluetooth;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;
import fr.isen.twinmx.util.TMSnackBar;

/**
 * Created by Clement on 19/01/2017.
 */

public abstract class BluetoothFragment extends Fragment {

    private TMBluetooth mBluetooth;

    private static boolean isFirstInit = true;

    public abstract CoordinatorLayout getCoordinatorLayout();

    private void checkBluetothState() {
        if (isFirstInit) {
            if (!mBluetooth.isBluetoothEnabled()) {
                showEnabledBluetoothSnackBar(); //Prompt to enable bluetooth. Once bluetooth is enabled, displays list of devices
            } else {
                if (!mBluetooth.isBluetoothConnectedToDevice()) {
                    mBluetooth.connectToKnownDevicesOrScanDevices();
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
                            mBluetooth.enableBluetooth();
                        }
                    }))
                    .show();
        }
    }

    public void onResume() {
        super.onResume();

        checkBluetothState();
        BluetoothFragment.isFirstInit = false;
    }

    public TMBluetooth getBluetooth() {
        return mBluetooth;
    }

    public void setBluetooth(TMBluetooth mBluetooth) {
        this.mBluetooth = mBluetooth;
    }
}
