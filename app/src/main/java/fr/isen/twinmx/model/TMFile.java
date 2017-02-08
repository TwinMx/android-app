package fr.isen.twinmx.model;

import java.io.File;

/**
 * Created by Clement on 08/02/2017.
 */

public class TMFile {

    private final String mName;
    private final File mFile;
    private final boolean mPaired;

    public TMFile(String name, File file, boolean paired) {
        mName = name;
        mFile = file;
        mPaired = paired;
    }

    public String getName() {
        return mName;
    }

    public File getFile() {
        return mFile;
    }

    public boolean isPaired() {
        return mPaired;
    }

    @Override
    public String toString() {
        return String.format("%1$s", mName);
    }
}
