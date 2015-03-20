import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import java.util.*;

public class HoughLines_ implements PlugInFilter {
    public static Point getIntersection(Image img, Line l1, Line l2) {
        return (null);
    }

    public static List<Point> getArea(Image img, List<Line> lines, int[] index) {
        List<Point> pts = new ArrayList<Point>();

        return (pts);
    }

    public static List<Line> getRect(Image img, List<Line> lines) {
        List<Line> rect = new ArrayList<Line>();
        int[] index = new int[4];

        for (index[0] = 0; index[0] < lines.size(); index[0]++) {
            for (index[1] = index[0] + 1; index[1] < lines.size(); index[1]++) {
                for (index[2] = index[1] + 1; index[2] < lines.size(); index[2]++) {
                    for (index[3] = index[2] + 1; index[3] < lines.size(); index[3]++) {
                        getArea(img, lines, index);
                    }
                }
            }
        }
        return (rect);
    }

    public void run(ImageProcessor ip) {
        Image img = new Image(ip);
        List<Line> lines = img.houghLines();
        img.displayLines(lines).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
