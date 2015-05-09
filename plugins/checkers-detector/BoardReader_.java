
import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import java.util.*;

public class BoardReader_ implements PlugInFilter {

    public static Image traverse(Image img){
        int rowNum = 8, colNum = 8;
        int[][][] histos = new int[rowNum][colNum][];
        int sqWidth = img.getWidth() / colNum, sqHeight = img.getHeight() / rowNum;
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                histos[i][j] = Histogram.getHisto(new Image(img, i*sqWidth, j*sqHeight, sqWidth, sqHeight));
            }
        }

        return Clusters.minimalLink(histos, 4);
    }

    public void run(ImageProcessor ip){
        Image img = new Image(ip);
        Board board = Solver.findCheckers(img);
        List<Point> rect = board.getCorners();
        int boardSize = board.getNbCase();

        img = Histogram.normalize(Transform.project(img, rect, 512));
        img.display();

        traverse(img).display();
    };

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G + DOES_RGB;
    }
};
