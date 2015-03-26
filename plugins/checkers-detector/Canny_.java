import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Canny_ implements PlugInFilter {
    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Options du flou gaussien");
        gd.addNumericField("Gaussian Filter radius", 2, 0);
        gd.addNumericField("Low threshold", 25, 0);
        gd.addNumericField("High threshold", 50, 0);
        gd.showDialog();
        if (gd.wasCanceled()) return;

        int radius = (int) gd.getNextNumber();
        int low = (int) gd.getNextNumber();
        int high = (int) gd.getNextNumber();

        EdgeDetection.canny(new Image(ip), radius, low, high).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
