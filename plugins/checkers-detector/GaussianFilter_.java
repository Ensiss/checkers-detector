import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class GaussianFilter_ implements PlugInFilter {
    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Options du flou gaussien");
        gd.addNumericField("Rayon du masque", 1, 0);
        gd.showDialog();
        if (gd.wasCanceled()) return;

        int radius = (int) gd.getNextNumber();
        Filter.gaussian(new Image(ip), radius).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
