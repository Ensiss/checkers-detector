import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import java.util.*;

public class Transform {
    public static double lerp(double a, double b, double x) {
        return (a + (b - a) * x);
    }

    public static Point lerp(Point a, Point b, double x) {
        return (new Point(lerp(a.x, b.x, x), lerp(a.y, b.y, x)));
    }

    public static Image project(Image img, List<Point> orig, int size) {
        Image out = new Image(size);

        for (int y = 0; y < out.getHeight(); y++) {
            for (int x = 0; x < out.getWidth(); x++) {
                double xratio = (double) x / (double) out.getWidth();
                double yratio = (double) y / (double) out.getHeight();
                yratio *= lerp((orig.get(1).x - orig.get(0).x) / (orig.get(3).x - orig.get(2).x), 1, yratio);
                Point upper = lerp(orig.get(0), orig.get(1), xratio);
                Point lower = lerp(orig.get(2), orig.get(3), xratio);
                Point pt = lerp(upper, lower, yratio);
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
}
