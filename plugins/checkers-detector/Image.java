import ij.*;
import ij.gui.*;
import ij.process.*;
import java.util.*;

public class Image {
    private double[][]  _data;
    private int         _width;
    private int         _height;

    public Image(int width, int height) {
        _width = width;
        _height = height;
        _data = new double[height][width];
    }

    public Image(int size) {
        this(size, size);
    }

    public Image(ImageProcessor ip) {
        this(ip.getWidth(), ip.getHeight());
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                put(x, y, ip.getPixel(x, y));
            }
        }
    }

    public Image(ImagePlus imp) {
        this(imp.getProcessor());
    }

    public List<Line> houghLines() {
        int maxDist = (int) Math.sqrt(_width * _width + _height * _height);

        Image edges = canny(2, 25, 50);
        edges.display();

        Image houghSpace = new Image(360, 2 * maxDist);

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                if (edges.get(x, y) == 0)
                    continue;
                for (int nx = 0; nx < 360; nx++) {
                    double a = nx * (Math.PI / 180.);
                    double d = x * Math.cos(a) + y * Math.sin(a);
                    int ny = (int) (d) + maxDist;

                    houghSpace._data[ny][nx]++;
                }
            }
        }

        houghSpace = houghSpace.normalize();
        houghSpace.display();
        houghSpace = houghSpace.threshold(150);
        houghSpace.display();

        List<Line> lines = new ArrayList<Line>();
        for (int y = 0; y < houghSpace.getHeight(); y++) {
            for (int x = 0; x < houghSpace.getWidth(); x++) {
                if (houghSpace.get(x, y) > 0)
                    lines.add(new Line(x * (Math.PI / 180.), y - maxDist));
            }
        }
        return (lines);
    }

    public Image displayLines(List<Line> lines) {
        ImagePlus imp = getImagePlus();
        ImageProcessor ip = imp.getProcessor();

        for (Line l : lines) {
            double a = l.getAngle();
            double d = l.getDist();
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

            ip.drawLine((int) p[0], (int) p[1],
                        (int) p[2], (int) p[3]);
        }
        return (new Image(ip));
    }

    private static int _cannyDoubleThreshold(int value, int low, int high) {
        if (value >= high) // Strong edge
            return (2);
        if (value >= low) // Weak edge
            return (1);
        return (0); // No edge
    }

    private Image _cannyHysteresis(int low, int high) {
        Image out = new Image(_width, _height);

        for (int y = 1; y < _height - 1; y++) {
            for (int x = 1; x < _width - 1; x++) {
                int inRange = _cannyDoubleThreshold((int) get(x, y), low, high);

                out.put(x, y, 0);
                if (inRange == 2) {
                    out.put(x, y, 255);
                    continue;
                }
                if (inRange == 0)
                    continue;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        if (i == 0 && j == 0) // Because Java is too dumb to allow (!i && !j)
                            continue;
                        if (_cannyDoubleThreshold((int) get(x + i, y + j), low, high) == 2) {
                            out.put(x, y, 255);
                            i = j = 1;
                        }
                    }
                }
            }
        }
        return (out);
    }

    public Image canny(int gaussianRadius, int low, int high) {
        // Gaussian filter
        Image gauss = gaussianFilter(gaussianRadius);

        // Sobel gradient
        Image sobX = gauss.sobelX();
        Image sobY = gauss.sobelY();
        Image grad = gradient(sobX, sobY);

        // Suppress non-maxima
        for (int y = 1; y < _height - 1; y++) {
            for (int x = 1; x < _width - 1; x++) {
                double angle = Math.atan2(sobY.get(x, y), sobX.get(x, y));
                if (angle < 0)
                    angle += Math.PI;
                angle = (int) (angle / (Math.PI / 180.0)) / 45;

                // Convert angle to neighbor pixel
                // 0 = right, 1 = up-right, 2 = up, 3 = up-left
                int dx = (new int[]{1, 1, 0, -1})[(int) angle % 4];
                int dy = (new int[]{0, 1, 1, 1})[(int) angle % 4];

                if (grad.get(x, y) > grad.get(x + dx, y + dy) &&
                    grad.get(x, y) > grad.get(x - dx, y - dy))
                    gauss.put(x, y, grad.get(x, y));
                else
                    gauss.put(x, y, 0);
            }
        }

        // Hysteresis
        return (gauss._cannyHysteresis(low, high));
    }

    public Image sharpen8() {
        return (sub(laplace8()));
    }

    public Image sharpen4() {
        return (sub(laplace4()));
    }

    public Image laplace4() {
        Mask mask = new Mask(1, new double[]{
                0,  1, 0,
                1, -4, 1,
                0,  1, 0
            });
        return (convolve(mask));
    }

    public Image laplace8() {
        Mask mask = new Mask(1, new double[]{
                1,  1, 1,
                1, -8, 1,
                1,  1, 1
            });
        return (convolve(mask));
    }

    public Image sobel() {
        return (gradient(sobelX(), sobelY()));
    }

    public Image sobelX() {
        Mask mask = new Mask(1, new double[]{
                -1, 0, 1,
                -2, 0, 2,
                -1, 0, 1
            });
        return (convolve(mask));
    }

    public Image sobelY() {
        Mask mask = new Mask(1, new double[]{
                -1, -2, -1,
                 0,  0,  0,
                 1,  2,  1
            });
        return (convolve(mask));
    }

    public Image averageFilter(int radius) {
        double value = 1. / Math.pow((radius * 2 + 1), 2);

        return (convolve(new Mask(radius, value)));
    }

    public Image gaussianFilter(int radius) {
        double s2 = Math.pow(radius / 3.0, 2);
        Mask mask = new Mask(radius);
        double sum = 0;

        for (int j = -radius; j <= radius; j++) {
            for (int i = -radius; i <= radius; i++) {
                mask.put(i, j, (1. / (2. * Math.PI * s2)) * Math.exp(-(i * i + j * j) / (2. * s2)));
                sum += mask.get(i, j);
            }
        }
        for (int j = -radius; j <= radius; j++) {
            for (int i = -radius; i <= radius; i++) {
                mask.put(i, j, mask.get(i, j) / sum);
            }
        }
        return (convolve(mask));
    }

    public Image medianFilter(int radius) {
        Image result = new Image(_width, _height);

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                if (x < radius || x >= _width - radius ||
                    y < radius || y >= _height - radius) {
                    result.put(x, y, get(x, y));
                    continue;
                }
                int[] pixels = new int[(int) Math.pow(radius * 2 + 1, 2)];
                for (int j = -radius; j <= radius; j++) {
                    for (int i = -radius; i <= radius; i++) {
                        pixels[(j + radius) * (radius * 2 + 1) + i + radius] = (int) get(x + i, y + j);
                    }
                }
                Arrays.sort(pixels);
                result.put(x, y, pixels[pixels.length / 2]);
            }
        }

        return (result);
    }

    public Image equalize() {
        Image result = new Image(_width, _height);
        int[] hist = new int[256];

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                hist[(int) get(x, y)]++;
            }
        }
        for (int i = 1; i < 256; i++)
            hist[i] += hist[i - 1];

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                int out = hist[(int) get(x, y)] * 256 / (_width * _height);
                result.set(x, y, out);
            }
        }
        return (result);
    }

    private static double _otsuSub(int[] histo, int start, int end, int npx) {
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

    public Image otsu() {
        int[] histo = new int[256];
        double[] withinClass = new double[256];

        for (int j = 0; j < _height; j++) {
            for (int i = 0; i < _width; i++) {
                histo[(int) get(i, j)]++;
            }
        }

        int     npx = _width * _height;
        int     mini = -1;
        for (int i = 0; i < 256; i++) {
            withinClass[i] = _otsuSub(histo, 0, i, npx) + _otsuSub(histo, i, 256, npx);
            if (mini == -1 || withinClass[i] < withinClass[mini])
                mini = i;
        }
        return (threshold(mini));
    }

    public Image threshold(int val) {
        Image result = new Image(_width, _height);

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                result.put(x, y, get(x, y) > val ? 255 : 0);
            }
        }
        return (result);
    }

    public Image threshold(int min, int max) {
        Image result = new Image(_width, _height);

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                int c = (int) get(x, y);
                result.put(x, y, c > min && c < max ? 255 : 0);
            }
        }
        return (result);
    }

    static public Image gradient(Image dx, Image dy) {
        Image result = new Image (dx.getWidth(), dx.getHeight());

        for (int y = 0; y < dx.getHeight(); y++)
            for (int x = 0; x < dx.getWidth(); x++)
                result.put(x, y, Math.sqrt(Math.pow(dx.get(x, y), 2) + Math.pow(dy.get(x, y), 2)));
        return (result);
    }

    public Image invert() {
        Image result = new Image(_width, _height);

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                result.put(x, y, 255 - get(x, y));
            }
        }
        return (result);
    }

    public Image add(Image img) {
        Image result = new Image(_width, _height);

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                result.put(x, y, get(x, y) + img.get(x, y));
            }
        }
        return (result);
    }

    public Image sub(Image img) {
        Image result = new Image(_width, _height);

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                result.put(x, y, get(x, y) - img.get(x, y));
            }
        }
        return (result);
    }

    public Image normalize() {
        Image result = new Image(_width, _height);

        double max = get(0, 0);
        double min = max;
        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                if (get(x, y) > max) max = get(x, y);
                if (get(x, y) < min) min = get(x, y);
            }
        }

        if (min != max) {
            for (int y = 0; y < _height; y++) {
                for (int x = 0; x < _width; x++) {
                    result.put(x, y, (int) ((255 * (get(x, y) - min)) / (max - min)));
                }
            }
        }
        return (result);
    }

    public Image convolve(Mask masque) {
        Image result = new Image(_width, _height);

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                if (x < masque.getRadius() || x >= _width - masque.getRadius() ||
                    y < masque.getRadius() || y >= _height - masque.getRadius())
                    result.put(x, y, get(x, y));
                else {
                    result.put(x, y, 0);
                    for (int j = -masque.getRadius(); j <= masque.getRadius(); j++) {
                        for (int i = -masque.getRadius(); i <= masque.getRadius(); i++) {
                            result.put(x, y, result.get(x, y) + get(x + i, y + j) * masque.get(i, j));
                        }
                    }
                }
            }
        }
        return (result);
    }

    public ImagePlus getImagePlus() {
        ImagePlus imp = NewImage.createByteImage("", _width, _height, 1, NewImage.FILL_BLACK);
        ImageProcessor ip = imp.getProcessor();

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                ip.putPixel(x, y, (int) Math.max(Math.min(get(x, y), 255), 0));
            }
        }
        return (imp);
    }

    public void display() {
        new ImageWindow(getImagePlus());
    }


    public int getWidth() {
        return (_width);
    }

    public int getHeight() {
        return (_height);
    }

    public double get(int x, int y) {
        return (_data[y][x]);
    }

    public void set(int x, int y, double value) {
        _data[y][x] = value;
    }

    public double getPixel(int x, int y) { return (get(x, y)); }
    public void put(int x, int y, double value) { set(x, y, value); }
    public void setPixel(int x, int y, double value) { set(x, y, value); }
    public void putPixel(int x, int y, double value) { set(x, y, value); }
}
