import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Erode_ implements PlugInFilter {
    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Options de la dilatation");
        gd.addNumericField("Dilatation radius", 1, 0);
        gd.showDialog();
        if (gd.wasCanceled()) return;

        int radius = (int) gd.getNextNumber();
        StructElement elt = new StructElement(StructElement.Type.CIRCLE, radius);
        Morphology.erode(new Image(ip), elt).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
