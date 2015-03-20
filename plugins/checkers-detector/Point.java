public class Point {
    public double x;
    public double y;

    public Point(double px, double py) {
        x = px;
        y = py;
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
}
