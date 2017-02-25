package fr.isen.twinmx.model;

/**
 * Created by Clement on 12/01/2017.
 */
public class RawData {
    private boolean firstAdded = false;
    private Integer msb;
    private Integer lsb;

    public RawData() {
    }

    private boolean isCorrectFrame(int b) {
        return (b%2 == 0) && ((b & 0xFF) < 128);
    }

    /**
     *
     * @param val
     * @return true if complete
     */
    public boolean add(int val) {
        if (!firstAdded) {
            this.msb = val;
            firstAdded = true;
            return false;
        }
        else {
            this.lsb = val;
            return true;

        }
    }

    public void clear() {
        firstAdded = false;
    }

    public double getRawMeasure() {
        double msbValue = this.msb << 5;
        double lsbValue = this.lsb >> 1;
        return (msbValue + lsbValue);
    }

    public double popMeasure() {
        double result = getRawMeasure();
        clear();
        return result;
    }
}
