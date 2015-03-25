
import java.lang.Math;
import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Shear_ implements PlugInFilter{

    public void run(ImageProcessor ip){
        Image i = hShear(new Image(ip), 0.5);
        i.display();
    }

    public static Image hShear(Image img, double a){
        Image result = new Image((int)(img.getWidth()+2*a), img.getHeight());
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                int newX = (int)(a*i+j);
                if(newX >= 0 && newX < result.getWidth()){
                    result.put(newX, i, img.get(j, i));
                }
            }
        }
        return result;
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }
}
