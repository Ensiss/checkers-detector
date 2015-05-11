
import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import java.util.*;

public class BoardReader_ implements PlugInFilter {

    public Image getCheckerboard (Image img, int size) {
        Image twoColors = Histogram.setContrast(img, 128), result = new Image(size, size);
        double width = twoColors.getWidth() / size, height = twoColors.getHeight() / size;
        double firstColor = 0.0, secondColor = 0.0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (y % 2 == x % 2)
                    firstColor += new Image(twoColors, (int)(x*width), (int)(y*height), (int)width, (int)height).getMeanColor();
                else
                    secondColor += new Image(twoColors, (int)(x*width), (int)(y*height), (int)width, (int)height).getMeanColor();
            }
        }
        firstColor /= size * size;
        secondColor /= size * size;
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                if (x % 2 == y % 2)
                    result.put(x, y, firstColor);
                else
                    result.put(x, y, secondColor);
            }
        }
        return Histogram.setContrast(Histogram.setBrightness(result, 64), 64);
    }

    public Image getCheckers (Image base, Image board) {
        int width = base.getWidth() / board.getWidth(), height = base.getHeight() / board.getHeight();
        Image result = new Image(board.getWidth(), board.getHeight());
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Image piece = Histogram.setContrast(new Image(base, x * width, y * height, width, height), 128);
                int whitePixels = 0, blackPixels = 0;
                for (int j = 0; j < piece.getHeight(); j++) {
                    for (int i = 0; i < piece.getWidth(); i++) {
                        if (piece.get(i, j) == 0)
                            blackPixels++;
                        else
                            whitePixels++;
                    }
                }
                if ((int)board.get(x, y) == 0 &&
                        whitePixels / (double)(whitePixels + blackPixels) >= 30/100.0)
                    result.put(x, y, 255);
                else if ((int)board.get(x, y) == 255 && blackPixels /
                        (double)(whitePixels + blackPixels) >= 30/100.0)
                    result.put(x, y, 128);
            }
        }
        return result;
    }

    public void run(ImageProcessor ip) {
        Image img = new Image(ip);
        Board board = Solver.findCheckers(img);
        List<Point> rect = board.getCorners();

        img = Histogram.normalize(Transform.project(img, rect, 512));
        img.display();
        Image checkerboard = getCheckerboard(img, board.getNbCase());
        checkerboard.display();
        getCheckers(img, checkerboard).display();

    };

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G + DOES_RGB;
    }
};
