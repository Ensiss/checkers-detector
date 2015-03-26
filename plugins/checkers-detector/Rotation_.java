import java.lang.Math;
import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Rotation_ implements PlugInFilter{

    public void run(ImageProcessor ip){
        Image i = new Image(ip);
        Image j = rotate(i, 45);
        j.display();
    }

    public static Image rotate(Image img, double a){
        a = Math.PI*a /180;
        return Shear_.vShear(
                Shear_.hShear(
                    Shear_.vShear(img, -Math.tan(a/2)),
                    Math.sin(a)),
                -Math.tan(a/2));
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }
}
