import ij.*;
import ij.plugin.filter.*;
import ij.process.*;

public class EdgeGradient_ implements PlugInFilter {
    public void run(ImageProcessor ip) {
        EdgeDetection.edgeGradient(new Image(ip), 20).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
