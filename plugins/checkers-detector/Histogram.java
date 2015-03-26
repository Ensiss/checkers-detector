public class Histogram {
    public static Image normalize(Image img) {
        Image result = new Image(img.getWidth(), img.getHeight());

        double max = img.get(0, 0);
        double min = max;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (img.get(x, y) > max) max = img.get(x, y);
                if (img.get(x, y) < min) min = img.get(x, y);
            }
        }

        if (min != max) {
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    result.put(x, y, (int) ((255 * (img.get(x, y) - min)) / (max - min)));
                }
            }
        }
        return (result);
    }

    public static Image equalize(Image img) {
        Image result = new Image(img.getWidth(), img.getHeight());
        int[] hist = new int[256];

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                hist[(int) img.get(x, y)]++;
            }
        }
        for (int i = 1; i < 256; i++)
            hist[i] += hist[i - 1];

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int out = hist[(int) img.get(x, y)] * 256 / (img.getWidth() * img.getHeight());
                result.set(x, y, out);
            }
        }
        return (result);
    }
}
