package linear_algebra;

import java.util.Arrays;

public class Vector {
    double[] data;
    private double length;

    Vector(double[] data, int length) {
        this.data = data;
        this.length = length;
    }

    static Vector zeros(int length) {
        double[] data = new double[length];
        for (int i=0; i<length; i++) {
            data[i] = 0;
        }

        return new Vector(data, length);
    }

    void set(int x, double value) {
        data[x] = value;
    }

    double get(int x) {
        return data[x];
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}
