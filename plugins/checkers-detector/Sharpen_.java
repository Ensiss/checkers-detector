import ij.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Sharpen_ implements PlugInFilter {
    public void run(ImageProcessor ip) {
        Filter.sharpen4(new Image(ip)).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
