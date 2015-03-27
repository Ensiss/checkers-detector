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

    public Image(Image img) {
        this(img, 0, 0);
    }

    public Image(Image img, int x, int y) {
        this(img, x, y, img.getWidth() - x, img.getHeight() - y);
    }

    public Image(Image img, int x, int y, int width, int height){
        this(width, height);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                put(j, i, img.get(x + j, y + i));
            }
        }
    }

    public Image drawLines(List<Line> lines) {
        ImagePlus imp = this.getImagePlus();
        ImageProcessor ip = imp.getProcessor();

        for (Line l : lines) {
            ip.drawLine((int) l.getStart().x, (int) l.getStart().y,
                        (int) l.getEnd().x, (int) l.getEnd().y);
        }
        return (new Image(ip));
    }

    public Image drawPts(List<Point> points) {
        ImagePlus imp = this.getImagePlus();
        ImageProcessor ip = imp.getProcessor();

        for (Point pt : points) {
            ip.fillOval((int) pt.x - 2, (int) pt.y - 2, 4, 4);
        }
        return (new Image(ip));
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
                            result.put(x, y, result.get(x, y) + get(x + i, y + j) * masque.get(-i, -j));
                        }
                    }
                }
            }
        }
        return (result);
    }

    public void erase(int x, int y, int radius){
        if(radius == 0 || x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight()){
            return;
        }
        this.put(x, y, 0);
        radius -= 1;
        erase(x+1, y, radius);
        erase(x-1, y, radius);
        erase(x, y+1, radius);
        erase(x, y-1, radius);
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

    public double[][] getData() {
        return (_data);
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
