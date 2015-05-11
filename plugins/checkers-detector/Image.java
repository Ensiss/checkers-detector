import ij.*;
import ij.gui.*;
import ij.process.*;
import java.util.*;

public class Image {
    private double[][]  _data;
    private int         _width;
    private int         _height;
    private String      _title;

    public Image(int width, int height) {
        _width = width;
        _height = height;
        _data = new double[height][width];
    }

    public Image(int width, int height, String title) {
        this(width, height);
        setTitle(title);
    }

    public Image(int size) {
        this(size, size);
    }

    public Image(ImageProcessor ip) {
        this(ip.getWidth(), ip.getHeight());
        if (ip.isGrayscale()) {
            for (int y = 0; y < ip.getHeight(); y++) {
                for (int x = 0; x < ip.getWidth(); x++) {
                    put(x, y, ip.getPixel(x, y));
                }
            }
        } else {
            for (int y = 0; y < ip.getHeight(); y++) {
                for (int x = 0; x < ip.getWidth(); x++) {
                    int[] rgb = new int[3];
                    ip.getPixel(x, y, rgb);
                    put(x, y, (rgb[0] + rgb[1] + rgb[2]) / 3);
                }
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
        this(width, height, img.getTitle());

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
            ip.fillOval((int) pt.x, (int) pt.y, 4, 4);
        }
        return (new Image(ip));
    }

    public Image drawBoard(Board b) {
        ImagePlus imp = this.getImagePlus();
        ImageProcessor ip = imp.getProcessor();
        int nbCase = b.getNbCase();

        ip.setColor(255);
        for (int i = 0; i < 9; i++) {
            ip.drawLine((int) b.get(0, i).x, (int) b.get(0, i).y,
                        (int) b.get(nbCase, i).x, (int) b.get(nbCase, i).y);
            ip.drawLine((int) b.get(i, 0).x, (int) b.get(i, 0).y,
                        (int) b.get(i, nbCase).x, (int) b.get(i, nbCase).y);
        }
        // for (int y = 0; y < 9; y++)
        //     for (int x = 0; x < 9; x++)
        //         ip.fillOval((int) b.get(x, y).x - 2, (int) b.get(x, y).y - 2, 4, 4);
        ip.setColor(0);
        return (new Image(ip));
    }

    public Image convolve(Mask m){
        return convolve(m, 0);
    }

    /**
     * @param int type : 0 clamp to edge, 1 mirrored repeat, 2 repeat, 3 clamp to border
     */
    public Image convolve(Mask mask, int type) {
        Image result = new Image(getWidth(), getHeight());

        for(int y = 0; y < getHeight(); y++){
            for(int x = 0; x < getWidth(); x++){
                if(!(type == 3 && (x < mask.getRadius() || x >= getWidth() - mask.getRadius() ||
                        y < mask.getRadius() || y >= getHeight() - mask.getRadius()))){
                    for(int j = -mask.getRadius(); j <= mask.getRadius(); j++){
                        for(int i = -mask.getRadius(); i <= mask.getRadius(); i++){
                            int newX = x + i;
                            int newY = y + j;
                            if(newX < 0 || newX >= getWidth()){
                                if(type == 0)
                                    newX = x - i;
                                else if(type == 1)
                                    newX = x - 2*i;
                                else if(type == 2)
                                    newX = (newX + getWidth()) % getWidth();
                            }
                            if(newY < 0 || newY >= getHeight()){
                                if(type == 0)
                                    newY = y - j;
                                else if(type == 1)
                                    newY = y - 2*j;
                                else if(type == 2)
                                    newY = (newY + getHeight()) % getHeight();
                            }
                            result.put(x, y, result.get(x, y) + get(newX, newY)*mask.get(i, j));
                        }
                    }
                }
            }
        }

        return result;
    }

    public void erase(int x, int y, int radius){
        if (radius == 0 || x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight())
            return;

        this.put(x, y, 0);
        radius -= 1;
        erase(x+1, y, radius);
        erase(x-1, y, radius);
        erase(x, y+1, radius);
        erase(x, y-1, radius);
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null || !(o instanceof Image))
            return false;

        Image img = Image.class.cast(o);
        if (getWidth() != img.getWidth() || getHeight() != img.getHeight())
            return false;

        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (get(j, i) != img.get(i, j))
                    return false;
            }
        }
        return true;
    }

    public double getMeanColor() {
        double result = 0.0;
        for (int j = 0; j < _height; j++) {
            for (int i = 0; i < _width; i++) {
                result += get(i, j);
            }
        }
        return result / (_height * _width);
    }

    public ImagePlus getImagePlus() {
        ImagePlus imp = NewImage.createByteImage("", _width, _height, 1, NewImage.FILL_BLACK);
        imp.setTitle(this._title);
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

    public String getTitle() {
        return (_title);
    }

    public void setTitle(Object s) {
        this._title = s == null ? "" : s.toString();
    }

    public double get(int x, int y) {
        return (_data[y][x]);
    }

    public int hashCode() {
        return Arrays.hashCode(getData());
    }

    public void set(int x, int y, double value) {
        _data[y][x] = value;
    }

    public double getPixel(int x, int y) { return (get(x, y)); }
    public void put(int x, int y, double value) { set(x, y, value); }
    public void setPixel(int x, int y, double value) { set(x, y, value); }
    public void putPixel(int x, int y, double value) { set(x, y, value); }
}
