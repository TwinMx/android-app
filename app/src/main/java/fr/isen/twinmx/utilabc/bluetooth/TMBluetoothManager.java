package fr.isen.twinmx.utilabc.bluetooth;

import android.app.Activity;

/**
 * Created by cdupl on 10/5/2016.
 */

public class TMBluetoothManager {

    private TMBluetooth bluetooth;


/*    private static TMBluetoothManager mBluetoothManagerInstance = null;
    public static TMBluetoothManager makeInstance(Activity activity) {
        return (mBluetoothManagerInstance = new TMBluetoothManager(activity));
    }

    public static TMBluetoothManager getInstance() {
        return mBluetoothManagerInstance;
    }*/

    public TMBluetoothManager(Activity activity) {
        this.bluetooth = new TMBluetooth(activity);
    }

    public TMBluetooth getBluetooth() {
        return this.bluetooth;
    }


}
