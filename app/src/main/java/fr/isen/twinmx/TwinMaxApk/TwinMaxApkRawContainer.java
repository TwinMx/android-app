package fr.isen.twinmx.TwinMaxApk;

import fr.isen.twinmx.fragments.TwinMaxApkChartComponent;

/**
 * Created by Clement on 25/01/2017.
 */
public class TwinMaxApkRawContainer {
    private ObservableArrayList<Byte> container;
    public FrameState rawFrameState;

    public boolean isRawContainerEmpty() {
        return container.isEmpty();
    }

    public TwinMaxApkRawContainer(TwinMaxApkChartComponent chartComponent) {
        container = new ObservableArrayList<Byte>(chartComponent);
        rawFrameState = FrameState.END;
    }

    public synchronized void addFrame(byte[] data, int size){
        if(container.size() >= 5000) {
            container.clear();
        }
        if(container != null && data != null) {
            for(byte b: data) {
                if(b == -1) {
                    return;
                }
                container.add(b);
            }
        }
    }
    public static boolean isFirst = true;
    public int getSize() {
        return container.size();
    }
    public synchronized byte getFirst() {
        byte b = 0;
        if(!isFirst) {
            container.remove(0);
            // container.remove(container.size()-1);

        }
        if(!container.isEmpty()) {
            b = container.get(0).byteValue();
            //   b = container.get(container.size()-1).byteValue();
        }
        isFirst = false;
        return b;
    }

    public void resetFrameState() {
        // Log.w("Reseting","RESETRESETRESETRESETRESET !!!");
        rawFrameState = FrameState.END;
    }

    public enum FrameState {
        Head,
        MSB1,
        LSB1,
        MSB2,
        LSB2,
        MSB3,
        LSB3,
        MSB4,
        LSB4,
        END
    }
}
