import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class HoughLines_ implements PlugInFilter {

    public static double[][] getResult(ImageProcessor ip)
    {
        ImagePlus imp = NewImage.createByteImage("Hough", ip.getWidth(), ip.getHeight(), 1, NewImage.FILL_WHITE);
        ImageProcessor ipOut = imp.getProcessor();

        int maxDist = (int) Math.sqrt(ip.getWidth() * ip.getWidth() + ip.getHeight() * ip.getHeight());
        int max = 0;

        // double[][] mat = ExtractionContours_.getResult(ip, 3, 127);
        double[][] mat = new double[ip.getWidth()][ip.getHeight()];
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                mat[x][y] = ip.getPixel(x, y);
            }
        }

        double[][] houghSpace = new double[360][2 * maxDist];

        for (int x = 0; x < mat.length; x++) {
            for (int y = 0; y < mat[x].length; y++) {
                if (mat[x][y] == 0)
                    continue;
                for (double a = 0; a < 2 * Math.PI; a += 2.0 * Math.PI / 360.) {
                    double d = x * Math.cos(a) + y * Math.sin(a);
                    // if (d < 0)
                    //     continue;

                    int nx = (int) (a / (Math.PI / 180.));
                    int ny = (int) (d) + maxDist;

                    houghSpace[nx][ny]++;
                    if (houghSpace[nx][ny] > max)
                        max = (int) houghSpace[nx][ny];
                }
            }
        }

        for (int x = 0; x < houghSpace.length; x++) {
            for (int y = 0; y < houghSpace[x].length; y++) {
                houghSpace[x][y] = houghSpace[x][y] / (double) max * 255.;
                houghSpace[x][y] = houghSpace[x][y] > 50 ? 255 : 0;
                if (houghSpace[x][y] > 0) {
                    double a = (double) x * (Math.PI / 180.);
                    double d = y - maxDist;

                    double[] p = new double[12];
                    int i = 0;

                    double tmpx, tmpy;

                    tmpx = 0;
                    tmpy = (d - tmpx * Math.cos(a)) / Math.sin(a);
                    if (tmpy >= 0 && tmpy < ip.getHeight()) {
                        p[i++] = tmpx; p[i++] = tmpy; }

                    tmpx = ip.getWidth() - 1;
                    tmpy = (d - tmpx * Math.cos(a)) / Math.sin(a);
                    if (tmpy >= 0 && tmpy < ip.getHeight()) {
                        p[i++] = tmpx; p[i++] = tmpy; }

                    tmpy = 0;
                    tmpx = (d - tmpy * Math.sin(a)) / Math.cos(a);
                    if (tmpx > 0 && tmpx < ip.getWidth()) {
                        p[i++] = tmpx; p[i++] = tmpy; }

                    tmpy = ip.getHeight() - 1;
                    tmpx = (d - tmpy * Math.sin(a)) / Math.cos(a);
                    if (tmpx > 0 && tmpx < ip.getWidth()) {
                        p[i++] = tmpx; p[i++] = tmpy; }

                    System.out.println(p[0] + ", " + p[1] + ", " + p[2] + ", " + p[3]);
                    ipOut.drawLine((int) p[0], (int) p[1],
                                   (int) p[2], (int) p[3]);
                }
            }
        }

        new ImageWindow(imp);
        return (houghSpace);
    }

    public void run(ImageProcessor ip) {
        Outils.afficheMatrice(getResult(ip), "HoughLines", false);
    }

    public int setup(String args, ImagePlus imp) {
        return NO_CHANGES + DOES_8G;
    }

}
