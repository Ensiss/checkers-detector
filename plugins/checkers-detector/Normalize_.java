import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Normalize_ implements PlugInFilter {
    public void run(ImageProcessor ip) {
        Histogram.normalize(new Image(ip)).display();
    }

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G;
    }
}
