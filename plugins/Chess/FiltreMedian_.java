import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class FiltreMedian_ implements PlugInFilter {

    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Options du filtre median");
        gd.addNumericField("Rayon du voisinage", 1, 0);
        gd.showDialog();
        if (gd.wasCanceled()) return;

        int radius = (int) gd.getNextNumber();
        new Image(ip).medianFilter(radius).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
