import java.util.*;

public class Filter {
    public static Image average(Image img, int radius) {
        double value = 1. / Math.pow((radius * 2 + 1), 2);

        return (img.convolve(new Mask(radius, value)));
    }

    public static Image median(Image img, int radius) {
        Image result = new Image(img.getWidth(), img.getHeight());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (x < radius || x >= img.getWidth() - radius ||
                    y < radius || y >= img.getHeight() - radius) {
                    result.put(x, y, img.get(x, y));
                    continue;
                }
                int[] pixels = new int[(int) Math.pow(radius * 2 + 1, 2)];
                for (int j = -radius; j <= radius; j++) {
                    for (int i = -radius; i <= radius; i++) {
                        pixels[(j + radius) * (radius * 2 + 1) + i + radius] = (int) img.get(x + i, y + j);
                    }
                }
                Arrays.sort(pixels);
                result.put(x, y, pixels[pixels.length / 2]);
            }
        }
        return (result);
    }

    public static Image gaussian(Image img, int radius) {
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
        return (img.convolve(mask));
    }

    public static Image sharpen8(Image img) {
        return (Arithmetic.sub(img, laplace8(img)));
    }

    public static Image sharpen4(Image img) {
        return (Arithmetic.sub(img, laplace4(img)));
    }

    public static Image laplace4(Image img) {
        Mask mask = new Mask(1, new double[]{
                0,  1, 0,
                1, -4, 1,
                0,  1, 0
            });
        return (img.convolve(mask));
    }

    public static Image laplace8(Image img) {
        Mask mask = new Mask(1, new double[]{
                1,  1, 1,
                1, -8, 1,
                1,  1, 1
            });
        return (img.convolve(mask));
    }
}
