import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import java.util.*;

public class Transform {
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

    public static Image rotate(Image img, double a){
        a = Math.PI*a /180;
        return vShear(
                      hShear(
                             vShear(img, -Math.tan(a/2)),
                             Math.sin(a)),
                      -Math.tan(a/2));
    }

    public static Image resize(Image img, int width, int height) {
        Image out = new Image(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double xratio = (double) x / (double) width;
                double yratio = (double) y / (double) height;
                Point pt = new Point(Utils.lerp(0, img.getWidth(), xratio),
                                     Utils.lerp(0, img.getHeight(), yratio));
                int px = (int) pt.x, py = (int) pt.y;
                Point frac = new Point(pt.x - px, pt.y - py);
                double col = 0;

                if (px == img.getWidth() - 1 || py == img.getHeight() - 1)
                    col = img.get(px, py);
                else {
                    col += img.get(px, py) * ((1 - frac.x) * (1 - frac.y));
                    col += img.get(px + 1, py) * (frac.x * (1 - frac.y));
                    col += img.get(px, py + 1) * ((1 - frac.x) * frac.y);
                    col += img.get(px + 1, py + 1) * (frac.x * frac.y);
                }
                out.put(x, y, col);
            }
        }
        return (out);
    }

    public static Image resize(Image img, int size) {
        double ratio = (double) img.getWidth() / (double) img.getHeight();
        Image out;

        if (ratio > 1.0) // width > height
            out = resize(img, size, (int) (size / ratio));
        else
            out = resize(img, (int) (size * ratio), size);
        return (out);
    }

    public static Image project(Image img, List<Point> orig, int size) {
        Image out = new Image(size);

        for (int y = 0; y < out.getHeight(); y++) {
            for (int x = 0; x < out.getWidth(); x++) {
                double xratio = (double) x / (double) out.getWidth();
                double yratio = (double) y / (double) out.getHeight();
                yratio *= Utils.lerp((orig.get(1).x - orig.get(0).x) / (orig.get(3).x - orig.get(2).x), 1, yratio);
                Point upper = Utils.lerp(orig.get(0), orig.get(1), xratio);
                Point lower = Utils.lerp(orig.get(2), orig.get(3), xratio);
                Point pt = Utils.lerp(upper, lower, yratio);
                int px = (int) pt.x, py = (int) pt.y;
                Point frac = new Point(pt.x - px, pt.y - py);
                double col = 0;

                if (px < 0 || py < 0 || px > img.getWidth() || py > img.getHeight())
                    col = 0;
                else if (px == img.getWidth() - 1 || py == img.getHeight() - 1)
                    col = img.get(px, py);
                else {
                    col += img.get(px, py) * ((1 - frac.x) * (1 - frac.y));
                    col += img.get(px + 1, py) * (frac.x * (1 - frac.y));
                    col += img.get(px, py + 1) * ((1 - frac.x) * frac.y);
                    col += img.get(px + 1, py + 1) * (frac.x * frac.y);
                }
                out.put(x, y, col);
            }
        }
        return (out);
    }
}
