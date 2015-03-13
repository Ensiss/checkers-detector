import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.filter.*;
import ij.process.*;

public class FiltreMoyen_ implements PlugInFilter {

    public void run(ImageProcessor ip) {

        GenericDialog gd = new GenericDialog("Options du filtre moyenneur");
        gd.addNumericField("Rayon du masque", 1, 0);
        gd.showDialog();
        if (gd.wasCanceled())
            return;

        int radius = (int) gd.getNextNumber();

        new Image(ip).averageFilter(radius).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
