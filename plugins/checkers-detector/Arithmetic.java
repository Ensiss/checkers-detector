public class Arithmetic {
    public static Image add(Image img, Image img2) {
        Image result = new Image(img.getWidth(), img.getHeight());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                result.put(x, y, img.get(x, y) + img2.get(x, y));
            }
        }
        return (result);
    }

    public static Image add(Image img, double val) {
        Image result = new Image(img.getWidth(), img.getHeight());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                result.put(x, y, img.get(x, y) + val);
            }
        }
        return (result);
    }

    public static Image sub(Image img, Image img2) {
        Image result = new Image(img.getWidth(), img.getHeight());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                result.put(x, y, img.get(x, y) - img2.get(x, y));
            }
        }
        return (result);
    }

    public static Image sub(Image img, double val) {
        Image result = new Image(img.getWidth(), img.getHeight());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                result.put(x, y, img.get(x, y) - val);
            }
        }
        return (result);
    }

    public static Image sub(double val, Image img) {
        Image result = new Image(img.getWidth(), img.getHeight());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                result.put(x, y, val - img.get(x, y));
            }
        }
        return (result);
    }

    public static Image hypot(Image dx, Image dy) {
        Image result = new Image (dx.getWidth(), dx.getHeight());

        for (int y = 0; y < dx.getHeight(); y++)
            for (int x = 0; x < dx.getWidth(); x++)
                result.put(x, y, Math.sqrt(Math.pow(dx.get(x, y), 2) + Math.pow(dy.get(x, y), 2)));
        return (result);
    }

    public static Image invert(Image img) {
        return (sub(255, img));
    }
}
