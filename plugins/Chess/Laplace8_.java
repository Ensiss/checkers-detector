import ij.*;
import ij.plugin.filter.*;
import ij.process.*;

/**
 * Rien a completer, juste a tester.
 * (Encore faut-il que la methode Outils.convoluer(...) fonctionne.)
 * Le masque de convolution est le suivant:
 *      | 1  1  1 |
 * L8 = | 1 -8  1 |
 *      | 1  1  1 |
 */
public class Laplace8_ implements PlugInFilter {

    public static double[][] getResult(ImageProcessor ip)
    {
        Masque laplace8 = new Masque(1, 1);

        laplace8.put(0, 0, -8);

        return (Outils.convoluer(ip, laplace8));
    }

    public void run(ImageProcessor ip) {
        Outils.afficheMatrice(getResult(ip), "Laplace 8", true);
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
