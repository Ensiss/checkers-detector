import java.util.*;

public class Solver {
    public static List<Point> getQuad(List<Line> lines, int[] index) {
        List<Point> pts = new ArrayList<Point>();
        Point[] intersect = new Point[3];
        int     oppositeIdx = 0;
        int[]   lrIdx = new int[3];
        int     lrCount = 0;

        // Get intersections between 1st line and the others
        for (int i = 1; i < 4; i++) {
            Point pt = lines.get(index[0]).intersect(lines.get(index[i]));
            if (pt != null) {
                pts.add(pt);
                lrIdx[lrCount++] = i;
            } else
                oppositeIdx = i;
        }

        if (lines.get(index[lrIdx[0]]).intersect(lines.get(index[lrIdx[1]])) != null)
            return (null);

        // We want a line to match exactly two other
        if (pts.size() != 2)
            return (null);

        // Intersect the opposite edge with the two edges
        for (int i = 0; i < 2; i++) {
            Point pt = lines.get(index[oppositeIdx]).intersect(lines.get(index[lrIdx[i]]));
            if (pt != null)
                pts.add(pt);
        }

        // We want 4 edges
        if (pts.size() != 4)
            return (null);

        return (Utils.sortPoints(pts));
    }

    public static double getQuadFitness(Image grad, List<Point> pts, int nbCase) {
        double mindist = 150;
        if (pts.get(0).dist(pts.get(1)) < mindist ||
            pts.get(0).dist(pts.get(2)) < mindist ||
            pts.get(0).dist(pts.get(3)) < mindist ||
            pts.get(3).dist(pts.get(1)) < mindist ||
            pts.get(3).dist(pts.get(2)) < mindist ||
            pts.get(1).dist(pts.get(2)) < mindist)
            return (0.0);

        Board board = new Board(pts.get(0), pts.get(1), pts.get(2), pts.get(3), nbCase);
        int width = 0;
        int height = 0;

        for (int i = 0; i < 4; i++) {
            width = (int) Math.max(width, pts.get(i).x);
            height = (int) Math.max(height, pts.get(i).y);
        }

        Image img = new Image(width, height).drawBoard(board);
        int npx = 0;
        double avg = 0;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (img.get(x, y) == 0 || x >= grad.getWidth() || y >= grad.getHeight())
                    continue;
                npx++;
                avg += grad.get(x, y);
            }
        }
        return (avg / (double) npx);
    }

    public static Board getBestQuad(Image grad, List<Line> lines) {
        int[] index = new int[4];

        List<Point> best8 = null;
        double maxFit8 = -1;
        List<Point> best10 = null;
        double maxFit10 = -1;

        for (index[0] = 0; index[0] < lines.size(); index[0]++) {
            for (index[1] = index[0] + 1; index[1] < lines.size(); index[1]++) {
                for (index[2] = index[1] + 1; index[2] < lines.size(); index[2]++) {
                    for (index[3] = index[2] + 1; index[3] < lines.size(); index[3]++) {
                        List<Point> pts = getQuad(lines, index);
                        if (pts == null)
                            continue;
                        double fit;

                        fit = getQuadFitness(grad, pts, 8);
                        if (maxFit8 == -1 || fit > maxFit8) {
                            maxFit8 = fit;
                            best8 = pts;
                        }

                        fit = getQuadFitness(grad, pts, 10);
                        if (maxFit10 == -1 || fit > maxFit10) {
                            maxFit10 = fit;
                            best10 = pts;
                        }

                    }
                }
            }
        }
        if (maxFit8 == -1)
            return (null);

        // System.out.println("8 cases: " + maxFit8 + ", 10 cases: " + maxFit10);
        // Valeurs pifomètre
        if (Math.abs(maxFit8 - maxFit10) < 5 || maxFit10 > 215 || maxFit10 > maxFit8)
            return (new Board(best10.get(0), best10.get(1), best10.get(2), best10.get(3), 10));
        return (new Board(best8.get(0), best8.get(1), best8.get(2), best8.get(3), 8));
    }

    public static Board findCheckers(Image img) {
        Image resized = Transform.resize(img, 500);
        List<Line> lines = LineDetection.hough(resized);
        Image grad = EdgeDetection.edgeGradient(resized, 10);
        Board board = getBestQuad(grad, lines);
        board.resize((double) img.getWidth() / (double) resized.getWidth());
        return (board);
    }
}
