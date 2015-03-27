import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import java.util.*;

public class HoughLines_ implements PlugInFilter {
    public static List<Point> getArea(Image img, List<Line> lines, int[] index) {
        List<Point> pts = new ArrayList<Point>();

        Point[] intersect = new Point[3];
        int     oppositeIdx = 0;
        // Get intersections between 1st line and the others
        for (int i = 1; i < 4; i++) {
            Point pt = lines.get(index[0]).intersect(lines.get(index[i]));
            if (pt != null)
                pts.add(pt);
            else
                oppositeIdx = i;
        }


        // We want a line to match exactly two other
        if (pts.size() != 2)
            return (null);

        // Intersect the opposite edge with the two edges
        for (int i = 1; i < 4; i++) {
            if (i != oppositeIdx) {
                Point pt = lines.get(index[i]).intersect(lines.get(index[oppositeIdx]));
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
        return (getTriangleArea(p1, p2, p3) + getTriangleArea(p2, p3, p4));
    }

    private static List<Point> sortPoints(List<Point> pts) {
        Point center = new Point(0, 0);

        for (Point p : pts) {
            center.x += p.x;
            center.y += p.y;
        }
        center.x /= pts.size();
        center.y /= pts.size();

        Collections.sort(pts, new Comparator<Point>() {
                public int compare(Point p1, Point p2) {
                    Double a1 = Math.atan2(p1.y - center.y, p1.x - center.x);
                    Double a2 = Math.atan2(p2.y - center.y, p2.x - center.x);
                    return (a1.compareTo(a2));
                }
            });
        pts.add(pts.remove(2));
        return (pts);
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
                        if (maxArea == -1 || area > maxArea) {
                            maxArea = area;
                            maxIndex = quadList.size();
                        }
                        quadList.add(pts);
                    }
                }
            }
        }

        if (maxIndex != -1)
            return (sortPoints(quadList.get(maxIndex)));
        return (null);
    }

    public void run(ImageProcessor ip) {
        Image img = new Image(ip);

        List<Line> lines = LineDetection.hough(img);
        List<Point> rect = new ArrayList<Point>();
        rect = getMaxArea(img, lines);

        // 3.tif
        // rect.add(new Point(135,45));
        // rect.add(new Point(505, 45));
        // rect.add(new Point(35, 340));
        // rect.add(new Point(605, 345));

        // 6.tif
        // rect.add(new Point(218, 28));
        // rect.add(new Point(548, 70));
        // rect.add(new Point(14, 113));
        // rect.add(new Point(517, 204));

        img.drawPts(rect).display();
        img.drawLines(lines).display();
        Transform.project(img, rect, 512).display();
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
