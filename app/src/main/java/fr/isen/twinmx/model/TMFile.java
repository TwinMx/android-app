package fr.isen.twinmx.model;

import java.io.File;

import fr.isen.twinmx.model.enums.TMDeviceType;

/**
 * Created by Clement on 08/02/2017.
 */

public class TMFile extends TMInput {

    private final String mFileName;

    public TMFile(String name, String fileName, boolean paired) {
        super(TMDeviceType.FILE, name, paired);
        mFileName = fileName;
    }

    public TMFile(File file, boolean paired) {
        this(file.getPath(), file.getAbsolutePath(), paired);
    }

    public String getFileName() {
        return mFileName;
    }

    @Override
    public String toString() {
        return String.format("%1$s", getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this.mFileName != null && obj != null && obj instanceof TMFile) {
            return this.mFileName.equals(((TMFile) obj).getFileName());
        }
        return super.equals(obj);
    }
}
