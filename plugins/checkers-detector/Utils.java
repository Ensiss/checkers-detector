import java.util.*;

public class Utils {
    public static double lerp(double a, double b, double x) {
        return (a + (b - a) * x);
    }

    public static Point lerp(Point a, Point b, double x) {
        return (new Point(lerp(a.x, b.x, x), lerp(a.y, b.y, x)));
    }

    public static List<Point> sortPoints(List<Point> pts) {
        final Point center = new Point(0, 0);

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

    public static List<Point> sortPoints(Point p1, Point p2, Point p3, Point p4) {
        List<Point> pts = new ArrayList<Point>();

        pts.add(p1);
        pts.add(p2);
        pts.add(p3);
        pts.add(p4);
        return (sortPoints(pts));
    }
}
