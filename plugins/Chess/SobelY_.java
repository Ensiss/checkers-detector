import ij.*;
import ij.plugin.filter.*;
import ij.process.*;

/**
 * Rien a completer, juste a tester.
 * (Encore faut-il que la methode Outils.convoluer(...) fonctionne.)
 * Le masque de convolution est le suivant:
 *      |-1 -2 -1 |
 * Sy = | 0  0  0 |
 *      | 1  2  1 |
 */
public class SobelY_ implements PlugInFilter {

    public static double[][] getResult(ImageProcessor ip)
    {
        Masque sobelY = new Masque(1, new double[]{
                -1, -2, -1,
                 0,  0,  0,
                 1,  2,  1
            });

        return (Outils.convoluer(ip, sobelY));
    }

    public void run(ImageProcessor ip) {
        Outils.afficheMatrice(getResult(ip), "Sobel Y", true);
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
