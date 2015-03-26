import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import java.util.*;

public class HoughLines_ implements PlugInFilter {
    public static Point getIntersection(Image img, Line l1, Line l2) {
        // L1: y = ax + b
        double a = -Math.cos(l1.getAngle()) / Math.sin(l1.getAngle());
        double b = l1.getDist() / Math.sin(l1.getAngle());
        // L2: y = mx + p
        double m = -Math.cos(l2.getAngle()) / Math.sin(l2.getAngle());
        double p = l2.getDist() / Math.sin(l2.getAngle());

        double x = (p - b) / (a - m);
        double y = a * x + b;

        // Out of bounds
        if (x < 0 || x >= img.getWidth() ||
            y < 0 || y >= img.getHeight())
            return (null);
        return (new Point(x, y));
    }

    public static List<Point> getArea(Image img, List<Line> lines, int[] index) {
        List<Point> pts = new ArrayList<Point>();

        Point[] intersect = new Point[3];
        int     oppositeIdx = 0;
        // Get intersections between 1st line and the others
        for (int i = 1; i < 4; i++) {
            Point pt = getIntersection(img, lines.get(index[0]), lines.get(index[i]));
            if (pt != null)
                pts.add(pt);
            else
                oppositeIdx = i;
        }


        // We want a line to match exactly two other
        if (pts.size() != 2)
            return (null);

        // Intersect the opposite edge with the two edges
        // Loop in reverse order so the corners order is consistent
        for (int i = 3; i > 0; i--) {
            if (i != oppositeIdx) {
                Point pt = getIntersection(img, lines.get(index[i]), lines.get(index[oppositeIdx]));
                if (pt != null)
                    pts.add(pt);
            }
        }

        // We want 4 edges
        if (pts.size() != 4)
            return (null);

        return (pts);
    }

    private static double getTriangleArea(Point p1, Point p2, Point p3) {
        // Heron's formula
        double a = p1.dist(p2);
        double b = p2.dist(p3);
        double c = p3.dist(p1);
        double s = (a + b + c) / 2.0;
        return (Math.sqrt(s * (s - a) * (s - b) * (s - c)));
    }

    private static double getQuadArea(Point p1, Point p2, Point p3, Point p4) {
        return (getTriangleArea(p1, p2, p3) + getTriangleArea(p1, p3, p4));
    }

    public static List<Point> getMaxArea(Image img, List<Line> lines) {
        int[] index = new int[4];

        List<List<Point>> quadList = new ArrayList<List<Point>>();
        double maxArea = -1;
        int maxIndex = -1;

        for (index[0] = 0; index[0] < lines.size(); index[0]++) {
            for (index[1] = index[0] + 1; index[1] < lines.size(); index[1]++) {
                for (index[2] = index[1] + 1; index[2] < lines.size(); index[2]++) {
                    for (index[3] = index[2] + 1; index[3] < lines.size(); index[3]++) {
                        List<Point> pts = getArea(img, lines, index);
                        if (pts == null)
                            continue;
                        double area = getQuadArea(pts.get(0), pts.get(1), pts.get(2), pts.get(3));
                        if (area > maxArea) {
                            maxArea = area;
                            maxIndex = quadList.size();
                        }
                        quadList.add(pts);
                    }
                }
            }
        }

        if (maxIndex != -1)
            return (quadList.get(maxIndex));
        return (null);
    }

    public void run(ImageProcessor ip) {
        Image img = new Image(ip);
        List<Line> lines = LineDetection.hough(img);
        img.drawLines(lines).display();
        img.drawPts(getMaxArea(img, lines)).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
