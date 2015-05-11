import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        int[] hist = getHisto(img);

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

    public static int[] getHisto(Image img) {
        int[] result = new int[256];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                result[(int)img.get(x, y)]++;
            }
        }
        return result;
    }

    public static Image setContrast(Image img, int contrast) {
        double[] contrastLookup = new double[256];
        double newValue = 0.0;
        double c = (100.0 + contrast) / 100.0;
        c *= c;
        for (int i = 0; i < contrastLookup.length; i++) {
            newValue = (double)i;
            newValue /= 255.0;
            newValue -= 0.5;
            newValue *= c;
            newValue += 0.5;
            newValue *= 255;

            if (newValue < 0)
                newValue = 0;
            else if (newValue > 255)
                newValue = 255;
            contrastLookup[i] = newValue;
        }

        Image result = new Image(img.getWidth(), img.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                result.put(x, y, contrastLookup[(int)img.get(x, y)]);
            }
        }
        return result;
    }

    public static Image setBrightness(Image img, int brightness) {
        double[] brightnessLookup = new double[256];
        double newValue = 0.0;

        for (int i = 0; i < brightnessLookup.length; i++) {
            newValue = i + brightness;

            if (brightness < 0)
                brightness = 0;
            else if (brightness > 255)
                brightness = 255;

            brightnessLookup[i] = newValue;
        }

        Image result = new Image(img.getWidth(), img.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                result.put(x, y, brightnessLookup[(int)img.get(x, y)]);
            }
        }
        return result;
    }

    public static Image reduceColors(Image img, int nb) {
        Image result = new Image(img.getWidth(), img.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                result.put(x, y, ((int)(img.get(x, y) * nb / 255)) * (255 / nb));
            }
        }
        return result;
    }

    public static Image smartReduceColors(Image img, int nb) throws Exception {
        int[] histo = getHisto(img);
        ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> allColors = new ArrayList<AbstractMap.SimpleEntry<Integer, Integer>>();
        for (int i = 1; i < histo.length -1; i++) {
            if ((histo[i-1] <= histo[i] && histo[i] >= histo[i+1]) || i == 0 || i == histo.length - 1) {
                allColors.add(new AbstractMap.SimpleEntry(new Integer(i), new Integer(histo[i])));
            }
        }
        Collections.sort(allColors, new Comparator<AbstractMap.SimpleEntry<Integer, Integer>>() {
            public int compare(AbstractMap.SimpleEntry<Integer, Integer> a, AbstractMap.SimpleEntry<Integer, Integer> b) {
                return -a.getValue().compareTo(b.getValue());
            };
        });
        if (allColors.size() < nb) {
            throw new Exception("Couldn't find enough peaks");
        }
        while (allColors.size() > nb) {
            allColors.remove(allColors.size() - 1);
        }
        Image result = new Image(img.getWidth(), img.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                double bestDist = Math.pow(10,10);
                int bestColor = 0;
                for (AbstractMap.SimpleEntry<Integer, Integer> color : allColors) {
                    double dist = Math.abs(color.getKey().doubleValue() - img.get(x, y));
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestColor = color.getKey();
                    }
                }
                result.put(x, y, bestColor);
            }
        }
        return result;
    }

}
