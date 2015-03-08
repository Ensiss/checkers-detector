import ij.*;
import ij.plugin.filter.*;
import ij.process.*;

/**
 * Rien a completer, juste a tester.
 * (Encore faut-il que la methode Outils.convoluer(...) fonctionne.)
 * Le masque de convolution est le suivant:
 *      |-1  0  1 |
 * Sx = |-2  0  2 |
 *      |-1  0  1 |
 */
public class SobelX_ implements PlugInFilter {

    public static double[][] getResult(ImageProcessor ip)
    {
        Masque sobelX = new Masque(1, new double[]{
                -1, 0, 1,
                -2, 0, 2,
                -1, 0, 1
            });

        return (Outils.convoluer(ip, sobelX));
    }

    public void run(ImageProcessor ip) {
        Outils.afficheMatrice(getResult(ip), "Sobel X", true);
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
