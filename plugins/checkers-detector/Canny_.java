import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Canny_ implements PlugInFilter {

    public void run(ImageProcessor ip) {
        new Image(ip).canny(50, 250).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
