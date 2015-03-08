import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Etirement_ implements PlugInFilter {
    public void run(ImageProcessor ip) {
        ImagePlus impEtir = NewImage.createByteImage("Etirement de l'histogramme",
                                                     ip.getWidth(), ip.getHeight(), 1, NewImage.FILL_BLACK);
        ImageProcessor ipEtir = impEtir.getProcessor();

        int     min = -1, max = -1;
        for (int j = 0; j < ip.getHeight(); j++) {
            for (int i = 0; i < ip.getWidth(); i++) {
                int px = ip.getPixel(i, j);
                if (min == -1 || px < min)
                    min = px;
                if (max == -1 || px > max)
                    max = px;
            }
        }

        for (int j = 0; j < ip.getHeight(); j++) {
            for (int i = 0; i < ip.getWidth(); i++) {
                int px = (ip.getPixel(i, j) - min) * 255 / (max - min);
                ipEtir.putPixel(i, j, px);
            }
        }

        /**
         * Fin de la partie a completer.
         */
        new ImageWindow(impEtir);
    }

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G;
    }
}
