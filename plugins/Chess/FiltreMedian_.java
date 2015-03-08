import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import java.util.*;

public class FiltreMedian_ implements PlugInFilter {

    public void run(ImageProcessor ip) {

        GenericDialog gd = new GenericDialog("Options du filtre median");
        gd.addNumericField("Rayon du voisinage", 1, 0);
        gd.showDialog();
        if (gd.wasCanceled()) return;

        // rayon: rayon du voisinage considere pour chaque pixel
        // Autrement dit, le voisinage est un carre de cote (2 * rayon + 1)
        int rayon = (int) gd.getNextNumber();

        ImagePlus impMedian = NewImage.createByteImage("Filtre median",
                ip.getWidth(), ip.getHeight(), 1, NewImage.FILL_BLACK);

        // ipMedian est l'image dans laquelle vous stockerez le
        // resultat du filtre median
        ImageProcessor ipMedian = impMedian.getProcessor();

        /**
         * A faire: appliquer un filtre median a l'image ip en considerant
         * un voisinage dont la taille est definie plus haut, et stoker le
         * resultat dans l'image 'ipMedian'.
         */

        // remplir ici
        for (int y = rayon; y < ip.getHeight() - rayon; y++) {
            for (int x = rayon; x < ip.getWidth() - rayon; x++) {
                int[] pixels = new int[(int) Math.pow(rayon * 2 + 1, 2)];
                for (int j = -rayon; j <= rayon; j++) {
                    for (int i = -rayon; i <= rayon; i++) {
                        pixels[(j + rayon) * (rayon * 2 + 1) + i + rayon] = ip.getPixel(x + i, y + j);
                    }
                }
                Arrays.sort(pixels);
                ipMedian.putPixel(x, y, pixels[(int) (pixels.length / 2.0)]);
            }
        }

        /**
         * Fin de la partie a completer
         */
        new ImageWindow(impMedian);

    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
