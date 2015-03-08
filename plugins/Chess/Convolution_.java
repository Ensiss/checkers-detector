import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Convolution_ implements PlugInFilter {
    public static int applyMatrix(ImageProcessor ip, int x, int y, double[][] matrix) {
        int     offset = matrix.length / 2;
        double  res = 0;

        for (int j = 0; j < matrix.length; j++) {
            for (int i = 0; i < matrix.length; i++) {
                res += ip.getPixel(x + i - offset, y + j - offset) * matrix[j][i];
            }
        }
        return ((int) res);
    }

    public void run(ImageProcessor ip) {
        ImagePlus impOut = NewImage.createByteImage("Otsu's threshold",
                                                    ip.getWidth(), ip.getHeight(), 1, NewImage.FILL_BLACK);
        ImageProcessor ipOut = impOut.getProcessor();
        double[][] matrix = new double[][] {
            {-1, -2, -1},
            {0, 0, 0},
            {1, 2, 1}
        };

        for (int j = 1; j < ip.getHeight() - 1; j++) {
            for (int i = 1; i < ip.getWidth() - 1; i++) {
                ipOut.putPixel(i, j, applyMatrix(ip, i, j, matrix));
            }
        }

        new ImageWindow(impOut);
    }

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G;
    }
}
