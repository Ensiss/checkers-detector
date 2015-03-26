public class Segmentation {
    public static Image threshold(Image img, int val) {
        Image result = new Image(img.getWidth(), img.getHeight());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                result.put(x, y, img.get(x, y) > val ? 255 : 0);
            }
        }
        return (result);
    }

    public static Image threshold(Image img, int min, int max) {
        Image result = new Image(img.getWidth(), img.getHeight());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int c = (int) img.get(x, y);
                result.put(x, y, c > min && c < max ? 255 : 0);
            }
        }
        return (result);
    }

    private static int _doubleThreshold(int value, int low, int high) {
        if (value >= high) // Strong edge
            return (2);
        if (value >= low) // Weak edge
            return (1);
        return (0); // No edge
    }

    public static Image hysteresis(Image img, int low, int high) {
        Image out = new Image(img.getWidth(), img.getHeight());

        for (int y = 1; y < img.getHeight() - 1; y++) {
            for (int x = 1; x < img.getWidth() - 1; x++) {
                int inRange = _doubleThreshold((int) img.get(x, y), low, high);

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
                        if (_doubleThreshold((int) img.get(x + i, y + j), low, high) == 2) {
                            out.put(x, y, 255);
                            i = j = 1;
                        }
                    }
                }
            }
        }
        return (out);
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

    public static Image otsu(Image img) {
        int[] histo = new int[256];
        double[] withinClass = new double[256];

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                histo[(int) img.get(i, j)]++;
            }
        }

        int     npx = img.getWidth() * img.getHeight();
        int     mini = -1;
        for (int i = 0; i < 256; i++) {
            withinClass[i] = _otsuSub(histo, 0, i, npx) + _otsuSub(histo, i, 256, npx);
            if (mini == -1 || withinClass[i] < withinClass[mini])
                mini = i;
        }
        return (threshold(img, mini));
    }

    //@TODO: Return an array of Points rather than an Image
    public static Image smartMax(Image img, int count){
        Image result = new Image(img.getWidth(), img.getHeight());
        Image tmp = new Image(img);
        for(int k = 0; k < count; k++){
            int x = 0, y = 0;
            double value = 0;
            for(int i = 0; i < tmp.getHeight(); i++){
                for(int j = 0; j < tmp.getWidth(); j++){
                    if(tmp.get(j, i) > value){
                        x = j;
                        y = i;
                        value = img.get(j, i);
                    }
                }
            }
            result.put(x, y, 255);
            tmp.erase(x, y, 10);
        }
        return result;
    }
}
