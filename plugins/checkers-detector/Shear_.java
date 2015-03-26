
import java.lang.Math;
import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Shear_ implements PlugInFilter{

    public void run(ImageProcessor ip){
        Image i = vShear(new Image(ip), -0.5);
        i.display();
    }

    public static Image hShear(Image img, double a){
        int offset = (int)(a*img.getHeight()+img.getWidth());
        Image result;
        if(offset > img.getWidth()){
            result = new Image(offset,img.getHeight());
            offset = 0;
        } else {
            offset = -offset;
            result = new Image(img.getWidth() + offset, img.getHeight());
        }
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

    public static Image vShear(Image img, double a){
        int offset = (int)(a*img.getWidth()+img.getHeight());
        Image result;
        if(offset > img.getHeight()){
            result = new Image(img.getWidth(),offset);
            offset = 0;
        } else {
            offset = -(offset-img.getHeight());
            result = new Image(img.getWidth(), img.getHeight() + offset);
        }
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                int newY = (int)(a*j+i) + offset;
                if(newY >= 0 && newY < result.getHeight()){
                    result.put(j, newY, img.get(j, i));
                }
            }
        }
        return result;
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }
}
