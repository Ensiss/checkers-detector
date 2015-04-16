public class Board {
    private Point[][] _data;

    public Board(Point ul, Point ur, Point ll, Point lr) {
        _data = new Point[9][9];
        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++)
                _data[y][x] = new Point();
        set(0, 0, ul);
        set(8, 0, ur);
        set(0, 8, ll);
        set(8, 8, lr);
        updatePoints();
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
        Point pt;
        for (int i = 1; i < 8; i++) {
            set(0, i, Utils.lerp(get(0, 0), get(0, 8), (double) i / 8.0));
            set(8, i, Utils.lerp(get(8, 0), get(8, 8), (double) i / 8.0));

            set(i, 0, Utils.lerp(get(0, 0), get(8, 0), (double) i / 8.0));
            set(i, 8, Utils.lerp(get(0, 8), get(8, 8), (double) i / 8.0));
        }
    }

    public void updateInside() {
        for (int y = 1; y < 8; y++) {
            for (int x = 1; x < 8; x++) {
                set(x, y, new Line(get(x, 0), get(x, 8)).intersect(new Line(get(0, y), get(8, y))));
            }
        }
    }

    public void set(int x, int y, Point pt) {
        _data[y][x].set(pt);
    }

    public Point get(int x, int y) {
        return (_data[y][x]);
    }
}
