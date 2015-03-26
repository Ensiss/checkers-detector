public class Utils {
    public static double lerp(double a, double b, double x) {
        return (a + (b - a) * x);
    }

    public static Point lerp(Point a, Point b, double x) {
        return (new Point(lerp(a.x, b.x, x), lerp(a.y, b.y, x)));
    }
}
