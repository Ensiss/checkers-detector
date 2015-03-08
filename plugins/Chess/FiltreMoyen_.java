import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.filter.*;
import ij.process.*;

public class FiltreMoyen_ implements PlugInFilter {

    public void run(ImageProcessor ip) {

        GenericDialog gd = new GenericDialog("Options du filtre moyenneur");
        gd.addNumericField("Rayon du masque", 1, 0);
        gd.showDialog();
        if (gd.wasCanceled())
            return;

        // rayon: rayon du masque moyenneur
        int rayon = (int) gd.getNextNumber();

        /**
         * A faire: convoluer l'image 'ip' avec un masque moyenneur. Entree: ip
         * (ImageProcessor) Sortie: mat (double[][]) Contrainte: la somme de
         * tous les elements du masque doit etre egale a 1, pour ne pas sortir
         * du domaine de definition des pixels [0..255]
         */
        // mat: une matrice pour stocker le resultat de la convolution
        double[][] mat = null;

        // remplir ici
        mat = Outils.convoluer(ip, new Masque(rayon, 1. / ((rayon * 2 + 1) * (rayon * 2 + 1))));

        /**
         * Fin de la partie a completer
         */
        // Affichage de la matrice
        if (mat != null) {
            Outils.afficheMatrice(mat, "Filtre moyen", false);
        }

    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
