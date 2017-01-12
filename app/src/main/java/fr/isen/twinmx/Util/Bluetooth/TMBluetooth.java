package fr.isen.twinmx.util.Bluetooth;

import android.util.Log;

import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.model.Measures;
import fr.isen.twinmx.model.RawMeasure;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by cdupl on 9/27/2016.
 */

public class TMBluetooth extends SmoothBluetooth {

    private static final ConnectionTo connectionTo = ConnectionTo.OTHER_DEVICE;
    private static final Connection connectionType = Connection.INSECURE;

    private static int HEADER = 128;

    private Device connectedDevice = null;

    private RawMeasure currentRawMeasure = new RawMeasure();
    private Measures measures = new Measures(4);


    public TMBluetooth(TMBluetoothListener listener) {
        super(TMApplication.getContext(), connectionTo, connectionType, listener);
        listener.setBluetooth(this);
    }

    public Device getConnectedDevice() {
        if (this.isConnected()) {
            return this.connectedDevice;
        }
        return null;
    }

    protected void setConnectedDevice(Device device) {
        this.connectedDevice = device;
    }

    public void onDataReceived(int data) {
        if (data == HEADER) {
            this.measures.clear();
            return;
        }

        this.currentRawMeasure.add(data);
        if (this.currentRawMeasure.isComplete()) {
            onMeasureReceived(this.currentRawMeasure.popMeasure());
        }
    }

    public void onMeasureReceived(double measure) {
        this.measures.add(measure);
        if (this.measures.isComplete()) {
            String values = "";
            for(Integer v : this.measures.getValues()) {
                values += v.toString() + " ";
            }
            Log.d("Measure", values);
            this.measures.clear();
        }
    }
}
