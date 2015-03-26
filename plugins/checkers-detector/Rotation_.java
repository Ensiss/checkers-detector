import java.lang.Math;
import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Rotation_ implements PlugInFilter{
    public void run(ImageProcessor ip){
        Transform.rotate(new Image(ip), 45).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }
}
