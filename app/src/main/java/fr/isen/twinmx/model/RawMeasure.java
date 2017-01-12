package fr.isen.twinmx.model;

/**
 * Created by Clement on 12/01/2017.
 */
public class RawMeasure {
    private Integer msb;
    private Integer lsb;

    private static double CONVERSION_FACTOR = 1.837;

    public RawMeasure() {
    }

    private boolean isCorrectFrame(int b) {
        return (b%2 == 0) && ((b & 0xFF) < 128);
    }

    public boolean add(int val) {
        if (this.msb == null) {
            this.msb = val;
            return true;
        }

        if (this.lsb == null) {
            this.lsb = val;
            return true;
        }

        return false;
    }

    public boolean isComplete() {
        return this.msb != null && this.lsb != null;
    }

    public void clear() {
        this.msb = null;
        this.lsb = null;
    }

    public double getMeasure() {
        double msbValue = this.msb << 5;
        double lsbValue = this.lsb >> 1;
        return (msbValue + lsbValue)/1.837;
    }

    public double popMeasure() {
        double result = getMeasure();
        clear();
        return result;
    }
}
