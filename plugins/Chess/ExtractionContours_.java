import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class ExtractionContours_ implements PlugInFilter {

    static public double[][] getResult(ImageProcessor ip, int rayon, int seuil)
    {
        ImagePlus impContours = NewImage.createByteImage("Contours",
                ip.getWidth(), ip.getHeight(), 1, NewImage.FILL_BLACK);

        ImageProcessor ipContours = impContours.getProcessor();

        double[][] mat = FiltreGaussien_.getResult(ip, rayon);

        // Convertir la matrice en image pour pouvoir la convoluer a nouveau
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                ipContours.putPixel(x, y, (int) mat[x][y]);
            }
        }

        mat = Sobel_.getResult(ipContours);

        for (int y = 0; y < ip.getHeight(); y++)
            for (int x = 0; x < ip.getWidth(); x++)
                mat[x][y] = mat[x][y] > seuil ? 255 : 0;

        return (mat);
    }

    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Options de l'extraction de contours");
        gd.addNumericField("Rayon du masque gaussien", 3, 0);
        gd.addNumericField("Seuil de binarisation", 128, 0);
        gd.showDialog();
        if (gd.wasCanceled()) return;

        int rayon = (int) gd.getNextNumber();
        int seuil = (int) gd.getNextNumber();

        Outils.afficheMatrice(getResult(ip, rayon, seuil), "Contours", false);

    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
