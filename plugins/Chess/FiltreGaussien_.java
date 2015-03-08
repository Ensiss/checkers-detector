import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class FiltreGaussien_ implements PlugInFilter {

    public static double[][] getResult(ImageProcessor ip, int rayon)
    {
        double sigma = rayon / 3.0;
        double sum = 0;

        double[][] mat = null;

        Masque mask = new Masque(rayon);
        for (int j = -rayon; j <= rayon; j++) {
            for (int i = -rayon; i <= rayon; i++) {
                mask.put(i, j, (1. / (2. * Math.PI * sigma * sigma)) * Math.exp(-(i * i + j * j) / (2. * sigma * sigma)));
                sum += mask.get(i, j);
            }
        }
        for (int j = -rayon; j <= rayon; j++) {
            for (int i = -rayon; i <= rayon; i++) {
                mask.put(i, j, mask.get(i, j) / sum);
            }
        }
        return (Outils.convoluer(ip, mask));
    }

    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Options du flou gaussien");
        gd.addNumericField("Rayon du masque", 1, 0);
        gd.showDialog();
        if (gd.wasCanceled()) return;

        Outils.afficheMatrice(getResult(ip, (int) gd.getNextNumber()), "Filtre gaussien", false);
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
