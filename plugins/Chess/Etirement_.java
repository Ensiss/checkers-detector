import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Etirement_ implements PlugInFilter {

    public void run(ImageProcessor ip) {
        new Image(ip).normalize().display();
    }

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G;
    }
}
