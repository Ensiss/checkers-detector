import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.filter.*;
import ij.process.*;

public class Equalize_ implements PlugInFilter {
    public void run(ImageProcessor ip) {
        Histogram.equalize(new Image(ip)).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
