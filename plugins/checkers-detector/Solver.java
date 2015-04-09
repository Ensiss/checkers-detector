public class Solver {
    public static Image findCheckers(Image img) {
        // Image tmp = Filter.gaussian(Filter.median(img, 2), 2);
        // Image sx = EdgeDetection.sobelX(tmp);
        // Image sy = EdgeDetection.sobelY(tmp);
        // Arithmetic.hypot(sx, sy).display();

        Board board = new Board(img);
        Image grad = EdgeDetection.edgeGradient(img, 20);
        grad.display();

        for (int it = 0; it < 10; it++) {
            Point forceUL = new Point();
            Point forceUR = new Point();
            Point forceLL = new Point();
            Point forceLR = new Point();
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    Point pt = board.get(x, y);

                    if (pt.x < 0 || pt.x >= grad.getWidth() ||
                        pt.y < 0 || pt.y >= grad.getHeight())
                        continue;

                    Point dir = new Point();
                    double cval = grad.get((int) pt.x, (int) pt.y);
                    double d = -1;

                    // Gradient direction
                    for (int j = -1; j <= 1; j++) {
                        for (int i = -1; i <= 1; i++) {
                            if (pt.x + i < 0 || pt.x + i >= grad.getWidth() ||
                                pt.y + j < 0 || pt.y + j >= grad.getHeight())
                                continue;

                            if (i == 0 && j == 0)
                                continue;
                            double tmpd = grad.get((int) pt.x + i, (int) pt.y + j) - cval;
                            if (tmpd > d) {
                                d = tmpd;
                                dir.x = i;
                                dir.y = j;
                            }
                        }
                    }

                    // Maximum
                    if (d <= 0)
                        continue;

                    if (x <= 4 && y < 4)
                        forceUL = forceUL.add(dir.mult((8 - x) * (8 - y) / 8.0));
                    else if (x > 4 && y <= 4)
                        forceUR = forceUR.add(dir.mult((x) * (8 - y) / 8.0));
                    else if (x < 4 && y >= 4)
                        forceLL = forceLL.add(dir.mult((8 - x) * (y) / 8.0));
                    else if (x >= 4 && y > 4)
                        forceLR = forceLR.add(dir.mult((x) * (y) / 8.0));
                }
            }
            // forceUL = forceUL.mult(10.0 / (9.0 * 9));
            // forceUR = forceUR.mult(10.0 / (9.0 * 9));
            // forceLL = forceLL.mult(10.0 / (9.0 * 9));
            // forceLR = forceLR.mult(10.0 / (9.0 * 9));

            board.set(0, 0, board.get(0, 0).add(forceUL));
            board.set(8, 0, board.get(8, 0).add(forceUR));
            board.set(0, 8, board.get(0, 8).add(forceLL));
            board.set(8, 8, board.get(8, 8).add(forceLR));
            board.updatePoints();
            img.drawBoard(board).display();
        }

        return (img.drawBoard(board));
    }
}
