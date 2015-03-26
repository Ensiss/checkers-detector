public class Morphology {
    public static Image dilate(Image img, StructElement elt) {
        Image out = new Image(img.getWidth(), img.getHeight());

        // For each pixel
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {

                // For each structuring element pixel
                for (int j = -elt.getRadius(); j <= elt.getRadius(); j++) {
                    for (int i = -elt.getRadius(); i <= elt.getRadius(); i++) {
                        // Outside of the element
                        if (elt.get(i, j) == 0)
                            continue;

                        // Out of bounds
                        if (x + i < 0 || x + i >= img.getWidth() ||
                            y + j < 0 || y + j >= img.getHeight())
                            continue;

                        // Add the pixel, go to next pixel
                        if (img.get(x + i, y + j) > 0) {
                            out.put(x, y, 255);
                            i = elt.getRadius();
                            j = elt.getRadius();
                        }
                    }
                }

            }
        }
        return (out);
    }

    public static Image erode(Image img, StructElement elt) {
        Image out = new Image(img.getWidth(), img.getHeight());

        // For each pixel
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {

                int fail = 0;
                // For each structuring element pixel
                for (int j = -elt.getRadius(); j <= elt.getRadius() && fail == 0; j++) {
                    for (int i = -elt.getRadius(); i <= elt.getRadius() && fail == 0; i++) {
                        // Outside of the element
                        if (elt.get(i, j) == 0)
                            continue;

                        // Out of bounds
                        if (x + i < 0 || x + i >= img.getWidth() ||
                            y + j < 0 || y + j >= img.getHeight()) {
                            fail++;
                            continue;
                        }

                        // Test failure
                        if (img.get(x + i, y + j) == 0)
                            fail++;
                    }
                }
                if (fail == 0)
                    out.put(x, y, 255);
            }
        }
        return (out);
    }

    public static Image open(Image img, StructElement elt) {
        return (dilate(erode(img, elt), elt));
    }

    public static Image close(Image img, StructElement elt) {
        return (erode(dilate(img, elt), elt));
    }
}
