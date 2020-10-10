package linear_algebra;

import java.util.Arrays;

public class Matrix {
    double[][] data;
    private int base;
    private int height;

    public Matrix(double[][] data, int base, int height) {
        this.data = data;
        this.base = base;
        this.height = height;
    }

    static Matrix zeros(int base, int height) {
        double[][] data = new double[height][base];
        for (int j=0; j<height; j++) {
            for (int i=0; i<base; i++) {
                data[j][i] = 0;
            }
        }
        return new Matrix(data, base, height);
    }

    double get(int x, int y) {
        return data[y][x];
    }

    void set(int x, int y, double value) {
        data[y][x] = value;
    }

    void swapRows(int i, int j) {
        double[] temp = data[j];
        data[j] = data[i];
        data[i] = temp;
    }

    static Matrix appendColumn(Matrix m, Vector v) {
        double[][] newData = new double[m.height][m.base+1];
        for (int j=0; j<m.height; j++) {
            for (int i=0; i<m.base; i++) {
                newData[j][i] = m.get(i, j);
            }
            newData[j][m.base] = v.get(j);
        }

        return new Matrix(newData, m.base+1, m.height);
    }

    private static Matrix identity(int size) {
        Matrix ret = Matrix.zeros(size, size);

        for (int i=0; i<size; i++) {
            ret.set(i, i, 1);
        }

        return ret;
    }

    private static Matrix augment(Matrix a, Matrix b) {
        assert a.height == b.height;
        int height = a.height;
        double[][] newData = new double[height][a.base+b.base];

        for (int j=0; j<height; j++) {
            for (int i=0; i<a.base; i++) {
                newData[j][i] = a.get(i, j);
            }
        }

        int offset = a.base;
        for (int j=0; j<height; j++) {
            for (int i=0; i<b.base; i++) {
                newData[j][i+offset] = b.get(i, j);
            }
        }

        return new Matrix(newData, a.base+b.base, height);
    }



    static Matrix invert(Matrix old) throws Exception {
        // augment this with the identity matrix
        // code taken from
        // https://www.codesansar.com/numerical-methods/matrix-inverse-using-gauss-jordan-method-pseudocode.htm
        assert old.height == old.base;
        int size = old.height;
        Matrix newMatrix = Matrix.augment(old, Matrix.identity(size));

        for (int i=0; i<size; i++) {
            if (newMatrix.get(i, i) == 0) {
                throw new Exception("bad");
            }

            for (int j=0; j<size; j++) {
                if (i != j) {
                    double f = newMatrix.get(i, j) / newMatrix.get(i, i);
                    for (int k=0; k<2*size; k++) {
                        newMatrix.set(k, j, newMatrix.get(k, j) - f*newMatrix.get(k, i));
                    }
                }
            }
        }

        for (int i=0; i<size; i++) {
            double tmp = newMatrix.get(i, i);
            for (int j=0; j<2*size; j++) {
                newMatrix.set(j, i, newMatrix.get(j,i) / tmp);
            }
        }

        double[][] retData = new double[size][size];
        for (int j=0; j<size; j++) {
            for (int i=0; i<size; i++) {
                retData[j][i] = newMatrix.get(i+size, j);
            }
        }

        return new Matrix(retData, size, size);
    }

    public Vector getLastColumn() {
        double[] data = new double[height];
        for (int j=0; j<height; j++) {
            data[j] = get(base-1, j);
        }
        return new Vector(data, height);
    }

    public void swapRows1(int i, int j) {
        double[] temp = data[j-1];
        data[j-1] = data[i-1];
        data[i-1] = temp;
    }

    public double get1(int y, int x) {
        return data[y-1][x-1];
    }

    public void set1(int y, int x, double value) {
        data[y-1][x-1] = value;
    }

    public Vector solve(Vector y) throws Exception {
        // https://en.wikipedia.org/wiki/Gaussian_elimination#Pseudocode
        // computes ret such that this*ret = y;
        Matrix augmented = appendColumn(this, y);
        int m = augmented.height;


        for (int k=0; k<augmented.height; k++) {
            int iMax = k;
            double max = Math.abs(augmented.data[iMax][k]);
            for (int i=k+1; i<m; i++) {
                double abs = Math.abs(augmented.data[i][k]);
                if (abs > max) {
                    iMax = i;
                    max = abs;
                }
            }

            if (augmented.data[iMax][k] == 0) {
                throw new Exception("boo");
            }

            augmented.swapRows(k, iMax);
            for (int i=k+1; i<m; i++) {
                double f = augmented.data[i][k] / augmented.data[k][k];
                for (int j=k+1; j<=m; j++) {
                    augmented.data[i][j] -= augmented.data[k][j]*f;
                }
                augmented.data[i][k] = 0;
            }
        }

        double[] ret = new double[m];
        for (int i=m-1; i>=0; i--) {
            ret[i] = augmented.data[i][m];
            for (int j=i+1; j<m; j++) {
                ret[i] -= augmented.data[i][j] * ret[j];
            }
            ret[i] /= augmented.data[i][i];
        }

        return new Vector(ret, m);
    }


    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        for (int j=0; j<height; j++) {
            for (int i=0; i<base; i++) {
                string.append(get(i, j) + ", ");
            }
            string.append('\n');
        }

        return string.toString();
    }
}
