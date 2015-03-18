import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.filter.*;
import ij.process.*;

public class Egalisation_ implements PlugInFilter {

    public void run(ImageProcessor ip) {
        new Image(ip).equalize().display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
