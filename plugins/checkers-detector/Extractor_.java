import ij.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Extractor_ implements PlugInFilter {
    public void run(ImageProcessor ip) {
        Solver.findCheckers(new Image(ip)).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
