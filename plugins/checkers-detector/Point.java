public class Point {
    public double x;
    public double y;

    public Point() {
        x = 0;
        y = 0;
    }

    public Point(double px, double py) {
        x = px;
        y = py;
    }

    public Point(Point pt) {
        set(pt);
    }

    public void set(Point pt) {
        x = pt.x;
        y = pt.y;
    }

    public double length() {
        return (Math.sqrt(x * x + y * y));
    }

    public double dist(Point p) {
        return (sub(p).length());
    }

    public Point add(Point p) {
        return (new Point(x + p.x, y + p.y));
    }

    public Point sub(Point p) {
        return (new Point(x - p.x, y - p.y));
    }

    public Point div(double d) {
        return (new Point(x / d, y / d));
    }

    public Point mult(double d) {
        return (new Point(x * d, y * d));
    }
}
