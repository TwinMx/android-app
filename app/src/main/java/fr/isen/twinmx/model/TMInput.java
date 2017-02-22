package fr.isen.twinmx.model;

/**
 * Created by Clement on 08/02/2017.
 */

public abstract class TMInput {

    private final TMDeviceType mType;
    private final String mName;
    private final boolean mPaired;

    public TMInput(TMDeviceType type, String name, boolean paired) {
        mType = type;
        mName = name;
        mPaired = paired;
    }

    public TMDeviceType getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public boolean isPaired() {
        return mPaired;
    }
}
