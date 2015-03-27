public class Line {
    private Point _start;
    private Point _end;

    public Line(double a, double d, Image img) {
        double[] p = new double[12];
        int i = 0;

        double tmpx, tmpy;

        tmpx = 0;
        tmpy = (d - tmpx * Math.cos(a)) / Math.sin(a);
        if (tmpy >= 0 && tmpy < img.getHeight()) {
            p[i++] = tmpx; p[i++] = tmpy; }

        tmpx = img.getWidth() - 1;
        tmpy = (d - tmpx * Math.cos(a)) / Math.sin(a);
        if (tmpy >= 0 && tmpy < img.getHeight()) {
            p[i++] = tmpx; p[i++] = tmpy; }

        tmpy = 0;
        tmpx = (d - tmpy * Math.sin(a)) / Math.cos(a);
        if (tmpx > 0 && tmpx < img.getWidth()) {
            p[i++] = tmpx; p[i++] = tmpy; }

        tmpy = img.getHeight() - 1;
        tmpx = (d - tmpy * Math.sin(a)) / Math.cos(a);
        if (tmpx > 0 && tmpx < img.getWidth()) {
            p[i++] = tmpx; p[i++] = tmpy; }

        _start = new Point(p[0], p[1]);
        _end = new Point(p[2], p[3]);
    }

    public Line(Point start, Point end) {
        _start = start;
        _end = end;
    }

    public void clip(Image mask) {
        double x, y;
        double dx = _end.x - _start.x;
        double dy = _end.y - _start.y;
        double steps = Math.abs(Math.max(dx, dy));
        dx /= steps;
        dy /= steps;

        // Clip line start
        x = _start.x;
        y = _start.y;
        for (int i = 0; i < steps; i++) {
            if (mask.get((int) x, (int) y) > 0) {
                _start.x = x;
                _start.y = y;
                break;
            }
            x += dx;
            y += dy;
        }

        // Clip line end
        x = _end.x;
        y = _end.y;
        for (int i = 0; i < steps; i++) {
            if (mask.get((int) x, (int) y) > 0) {
                _end.x = x;
                _end.y = y;
                break;
            }
            x -= dx;
            y -= dy;
        }
    }

    public Point intersect(Line l) {
        double x1 = _start.x, y1 = _start.y;
        double x2 = _end.x, y2 = _end.y;
        double x3 = l._start.x, y3 = l._start.y;
        double x4 = l._end.x, y4 = l._end.y;

        double det = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        // Parallel lines
        if (Math.abs(det) < 0.0001)
            return (null);

        double dx = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4);
        double dy = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4);
        Point pt = new Point(dx / det, dy / det);

        // Intersection out of the segment
        if (pt.x < Math.min(_start.x, _end.x) ||
            pt.x > Math.max(_start.x, _end.x) ||
            pt.y < Math.min(_start.y, _end.y) ||
            pt.y > Math.max(_start.y, _end.y))
            return (null);

        return (pt);
    }

    public Point getStart() {
        return (_start);
    }

    public Point getEnd() {
        return (_end);
    }
}
