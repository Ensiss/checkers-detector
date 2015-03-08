import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Canny_ implements PlugInFilter {

    public static double[][] getResult(ImageProcessor ip)
    {
        ImagePlus imOut = NewImage.createByteImage("Hough", ip.getWidth(), ip.getHeight(), 1, NewImage.FILL_WHITE);
        ImageProcessor ipOut = imOut.getProcessor();

        // Apply Gaussian filter
        double[][] mat = FiltreGaussien_.getResult(ip, 5);

        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                ipOut.putPixel(x, y, (int) mat[x][y]);
            }
        }

        // Compute sobel gradient
        double[][] sobelX = SobelX_.getResult(ipOut);
        double[][] sobelY = SobelY_.getResult(ipOut);
        double[][] grad = new double[sobelX.length][sobelX[0].length];

        for (int j = 0; j < sobelX.length; j++)
            for (int i = 0; i < sobelX[j].length; i++)
                grad[j][i] = Math.sqrt(Math.pow(sobelX[j][i], 2) + Math.pow(sobelY[j][i], 2));

        // Suppress non-maxima
        for (int y = 1; y < ipOut.getHeight() - 1; y++) {
            for (int x = 1; x < ipOut.getWidth() - 1; x++) {
                double angle = Math.atan2(sobelY[x][y], sobelX[x][y]);
                if (angle < 0)
                    angle += Math.PI;
                angle = (int) (angle / (Math.PI / 180.0)) / 45 + 2;

                // Convert angle to neighbor pixel
                // 0 = right, 1 = up-right, 2 = up, 3 = up-left
                int dx = (new int[]{1, 1, 0, -1})[(int) angle % 4];
                int dy = (new int[]{0, -1, -1, -1})[(int) angle % 4];

                if (grad[x][y] > grad[x + dx][y + dy] &&
                    grad[x][y] > grad[x - dx][y - dy])
                    mat[x][y] = grad[x][y];
                else
                    mat[x][y] = 0;
            }
        }

        return (mat);
    }

    public void run(ImageProcessor ip) {
        // GenericDialog gd = new GenericDialog("Options du flou gaussien");
        // gd.addNumericField("Rayon du masque", 1, 0);
        // gd.showDialog();
        // if (gd.wasCanceled()) return;

        Outils.afficheMatrice(getResult(ip), "Filtre gaussien", false);
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
