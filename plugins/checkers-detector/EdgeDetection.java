public class EdgeDetection {
    public static Image canny(Image img, int gaussianRadius, int low, int high) {
        // Gaussian filter
        Image gauss = Filter.gaussian(img, gaussianRadius);

        // Sobel gradient
        Image sobX = EdgeDetection.sobelX(gauss);
        Image sobY = EdgeDetection.sobelY(gauss);
        Image grad = Arithmetic.hypot(sobX, sobY);

        // Suppress non-maxima
        for (int y = 1; y < img.getHeight() - 1; y++) {
            for (int x = 1; x < img.getWidth() - 1; x++) {
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
        return (Segmentation.hysteresis(gauss, low, high));
    }

    public static Image sobel(Image img) {
        return (Arithmetic.hypot(sobelX(img), sobelY(img)));
    }

    public static Image sobelX(Image img) {
        Mask mask = new Mask(1, new double[]{
                -1, 0, 1,
                -2, 0, 2,
                -1, 0, 1
            });
        return (img.convolve(mask));
    }

    public static Image sobelY(Image img) {
        Mask mask = new Mask(1, new double[]{
                -1, -2, -1,
                 0,  0,  0,
                 1,  2,  1
            });
        return (img.convolve(mask));
    }

    public static Image edgeGradient(Image img, int radius) {
        img = EdgeDetection.canny(Filter.median(img, 2), 2, 25, 50);
        Image out = new Image(img.getWidth(), img.getHeight());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                double dist = -1;
                for (int r = 0; r < radius && dist == -1; r++) {
                    for (int i = -r; i <= r && dist == -1; i++) {
                        if (x + i < 0 || x + i >= img.getWidth())
                            continue;
                        if ((y - r >= 0 && y - r < img.getHeight() && img.get(x + i, y - r) == 255) ||
                            (y + r >= 0 && y + r < img.getHeight() && img.get(x + i, y + r) == 255))
                            dist = r;
                    }
                    for (int j = -r + 1; j <= r - 1 && dist == -1; j++) {
                        if (y + j < 0 || y + j >= img.getHeight())
                            continue;
                        if ((x - r >= 0 && x - r < img.getWidth() && img.get(x - r, y + j) == 255) ||
                            (x + r >= 0 && x + r < img.getWidth() && img.get(x + r, y + j) == 255))
                            dist = r;
                    }
                }
                if (dist == -1)
                    out.put(x, y, 0);
                else
                    out.put(x, y, 255 - (255.0 * dist / (double) radius));
            }
        }
        return (out);
    }
}
