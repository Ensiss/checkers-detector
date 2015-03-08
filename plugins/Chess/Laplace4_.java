import ij.*;
import ij.plugin.filter.*;
import ij.process.*;

/**
 * Rien a completer, juste a tester.
 * (Encore faut-il que la methode Outils.convoluer(...) fonctionne.)
 * Le masque de convolution est le suivant:
 *      | 0  1  0 |
 * L4 = | 1 -4  1 |
 *      | 0  1  0 |
 */
public class Laplace4_ implements PlugInFilter {

    static public double[][] getResult(ImageProcessor ip)
    {
        Masque laplace4 = new Masque(1);

        laplace4.put(0, -1, 1);
        laplace4.put( -1, 0, 1);
        laplace4.put(0, 0, -4);
        laplace4.put(1, 0, 1);
        laplace4.put(0, 1, 1);

        return (Outils.convoluer(ip, laplace4));
    }

    public void run(ImageProcessor ip) {
        Outils.afficheMatrice(getResult(ip), "Laplace 4", true);
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
