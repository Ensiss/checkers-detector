import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;

public class Otsu_ implements PlugInFilter {
    public static double computeThings(int[] histo, int start, int end, int npx) {
        double  w = 0;
        double  u = 0;
        double  o = 0;
        double  sum = 0;

        if (start == end)
            return (0);
        for (int i = start; i < end; i++) {
            sum += histo[i];
            u += i * histo[i];
        }
        w = sum / npx;
        u /= sum;
        for (int i = start; i < end; i++)
            o += ((double) i - u) * ((double) i - u) * histo[i];
        o /= sum;
        return (w * o);
    }

    public void run(ImageProcessor ip) {
        ImagePlus impOut = NewImage.createByteImage("Otsu's threshold",
                                                    ip.getWidth(), ip.getHeight(), 1, NewImage.FILL_BLACK);
        ImageProcessor ipOut = impOut.getProcessor();

        int[]   histo = new int[256];
        for (int j = 0; j < ip.getHeight(); j++) {
            for (int i = 0; i < ip.getWidth(); i++) {
                histo[ip.getPixel(i, j)]++;
            }
        }

        double[] withinClass = new double[256];
        int     npx = ip.getWidth() * ip.getHeight();
        int     mini = -1;
        for (int i = 0; i < 256; i++) {
            withinClass[i] = computeThings(histo, 0, i, npx) + computeThings(histo, i, 256, npx);
            if (mini == -1 || withinClass[i] < withinClass[mini])
                mini = i;
        }

        for (int j = 0; j < ip.getHeight(); j++) {
            for (int i = 0; i < ip.getWidth(); i++) {
                ipOut.putPixel(i, j, ip.getPixel(i, j) > mini ? 255 : 0);
            }
        }

        new ImageWindow(impOut);
    }

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G;
    }
}
