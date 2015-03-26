
import java.lang.Math;
import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Shear_ implements PlugInFilter{
    public void run(ImageProcessor ip){
        Transform.vShear(new Image(ip), -0.5).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }
}
