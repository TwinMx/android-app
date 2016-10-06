package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.isen.twinmx.R;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;
import fr.isen.twinmx.util.TMSnackBar;

/**
 * Created by pierredfc.
 */
public class BluetoothFragment extends Fragment {

    private View rootview;

    private CoordinatorLayout coordinatorLayout;

    private final int REQUEST_ENABLE_BT = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootview = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_acquisition));

        return this.rootview;
    }

    @Override
    public void onResume() {
        super.onResume();

        //TMBluetooth bluetooth = new TMBluetooth(this, new TMBluetoothListener(this));
        //bluetooth.tryConnection();


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            // Device supports Bluetooth
            if (!bluetoothAdapter.isEnabled()) {
                promptEnableBluetooth();
            }
            else {
                TMBluetoothManager.getInstance().getBluetooth().tryConnection();
            }
            /*else {
                //showPairedBluetoothDevices();
                discoverBluetoothDevices();
            }
        } else {
            // Device does not support Bluetooth
            */
        }


    }

    public void promptEnableBluetooth() {
        this.coordinatorLayout = this.coordinatorLayout == null ? (CoordinatorLayout) this.getActivity().findViewById(R.id.bluetoothContainer) : this.coordinatorLayout;
        (TMSnackBar
                .makeBluetooth(this.getActivity().getApplicationContext(), this.coordinatorLayout,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                //showPairedBluetoothDevices();
                                //TMBluetoothManager.getInstance().discoverBluetoothDevices();
                                TMBluetoothManager.getInstance().getBluetooth().tryConnection();
                            }
                        }))
                .show();
    }




}
