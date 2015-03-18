import ij.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Sobel_ implements PlugInFilter {

    public void run(ImageProcessor ip) {
        new Image(ip).sobel().display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
