import java.lang.Math;
import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Rotation_ implements PlugInFilter{

    public void run(ImageProcessor ip){
        Image i = new Image(ip);
        Image j = rotate(i, 90);
        Image k = rotate(i, 180);
        Image l = rotate(i, 270);
        Image m = rotate(i, 360);
        Image n = rotate(i, 0);
        j.display();
        k.display();
        l.display();
        m.display();
        n.display();
    }

    public static Image rotate(Image img, double a){
        a = a*Math.PI/180;
        int w = img.getWidth(), h = img.getHeight();
        Image r = new Image(w*2, h*2);
        for(int i = 0; i < h; i++){
            for(int j = 0; j < w; j++){
                int x = (int)(j*Math.cos(a)+i*Math.sin(a))+w;
                int y = (int)(-j*Math.sin(a)+i*Math.cos(a))+h;
                if(x > 0 && x < w*2 && y > 0 && y < h*2){
                    r.put(x, y, img.get(j, i));
                }
            }
        }
        return r;
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }
}
