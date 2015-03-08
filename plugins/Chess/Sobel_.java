import ij.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Sobel_ implements PlugInFilter {

    public static double[][] getResult(ImageProcessor ip)
    {
        double[][] matX = SobelX_.getResult(ip);
        double[][] matY = SobelY_.getResult(ip);

        for (int j = 0; j < matX.length; j++)
            for (int i = 0; i < matX[j].length; i++)
                matX[j][i] = Math.sqrt(Math.pow(matX[j][i], 2) + Math.pow(matY[j][i], 2));

        return (matX);
    }

    public void run(ImageProcessor ip) {
        /**
         * A faire: remplir la matrice mat avec la norme
         * du gradient de Sobel de l'image 'ip'.
         * Entree: ip (ImageProcessor)
         * Sortie: mat (double[][])
         * Contrainte: utiliser la norme euclidienne.
         */

        Outils.afficheMatrice(getResult(ip), "Sobel", true);
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
