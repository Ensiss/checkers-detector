import java.util.*;

public class Board {
    private Point[][] _data;
    private int _nbCase;

    public Board(Point ul, Point ur, Point ll, Point lr, int nbCase) {
        _nbCase = nbCase;
        _data = new Point[_nbCase + 1][_nbCase + 1];
        for (int y = 0; y < _nbCase + 1; y++)
            for (int x = 0; x < _nbCase + 1; x++)
                _data[y][x] = new Point();
        set(0, 0, ul);
        set(_nbCase, 0, ur);
        set(0, _nbCase, ll);
        set(_nbCase, _nbCase, lr);
        updatePoints();
    }

    public Board(Point ul, Point ur, Point ll, Point lr) {
        this(ul, ur, ll, lr, 10);
    }

    public Board(Image img) {
        this(new Point(0, 0),
             new Point(img.getWidth() - 1, 0),
             new Point(0, img.getHeight() - 1),
             new Point(img.getWidth() - 1, img.getHeight() - 1));
    }

    public void updatePoints() {
        updateEdges();
        // updateInside();
    }

    public void updateEdges() {
        for (int y = 1; y < _nbCase; y++) {
            double yratio = (double) y / (double) _nbCase;
            yratio *= Utils.lerp(get(_nbCase, 0).dist(get(0, 0)) / get(_nbCase, _nbCase).dist(get(0, _nbCase)), 1, yratio);

            set(0, y, Utils.lerp(get(0, 0), get(0, _nbCase), yratio));
            set(_nbCase, y, Utils.lerp(get(_nbCase, 0), get(_nbCase, _nbCase), yratio));
        }

        for (int x = 1; x < _nbCase; x++) {
            double xratio = (double) x / (double) _nbCase;
            xratio *= Utils.lerp(get(0, _nbCase).dist(get(0, 0)) / get(_nbCase, _nbCase).dist(get(_nbCase, 0)), 1, xratio);

            set(x, 0, Utils.lerp(get(0, 0), get(_nbCase, 0), xratio));
            set(x, _nbCase, Utils.lerp(get(0, _nbCase), get(_nbCase, _nbCase), xratio));
        }
    }

    public void updateInside() {
        for (int y = 1; y < _nbCase; y++) {
            for (int x = 1; x < _nbCase; x++) {
                set(x, y, new Line(get(x, 0), get(x, _nbCase)).intersect(new Line(get(0, y), get(_nbCase, y))));
            }
        }
    }

    public void set(int x, int y, Point pt) {
        _data[y][x].set(pt);
    }

    public Point get(int x, int y) {
        return (_data[y][x]);
    }

    public void resize(double ratio) {
        _data[0][0] = _data[0][0].mult(ratio);
        _data[0][_nbCase] = _data[0][_nbCase].mult(ratio);
        _data[_nbCase][0] = _data[_nbCase][0].mult(ratio);
        _data[_nbCase][_nbCase] = _data[_nbCase][_nbCase].mult(ratio);
        updatePoints();
    }

    public List<Point> getCorners() {
        List<Point> pts = new ArrayList<Point>();

        pts.add(get(0, 0));
        pts.add(get(_nbCase, 0));
        pts.add(get(0, _nbCase));
        pts.add(get(_nbCase, _nbCase));
        return (pts);
    }

    public int getNbCase() {
        return (_nbCase);
    }
}
