import ij.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Rehaussement_ implements PlugInFilter {

    public static double[][] getResult(ImageProcessor ip)
    {
        double[][] mat = Laplace8_.getResult(ip);
        for (int y = 0; y < mat.length; y++)
            for (int x = 0; x < mat[y].length; x++)
                mat[y][x] = ip.getPixel(y, x) - mat[y][x];
        return (mat);
    }

    public void run(ImageProcessor ip) {
        /**
         * A faire: rehausser les contours de l'image 'ip' par la methode
         * de la soustraction du laplacien.
         * Entree: ip (ImageProcessor)
         * Sortie: mat (double[][])
         */

        Outils.afficheMatrice(getResult(ip), "Contours rehausses", false);
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
