import ij.*;
import ij.gui.*;
import ij.process.*;
import java.util.*;

public class LineDetection {
    public static List<Line> hough(Image img) {
        int maxDist = (int) Math.sqrt(img.getWidth() * img.getWidth() + img.getHeight() * img.getHeight());

        Image edges = EdgeDetection.canny(Filter.median(img, 2), 2, 25, 50);

        Image houghSpace = new Image(360, 2 * maxDist);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (edges.get(x, y) == 0)
                    continue;
                for (int nx = 0; nx < 360; nx++) {
                    double a = nx * (Math.PI / 180.);
                    double d = x * Math.cos(a) + y * Math.sin(a);
                    int ny = (int) (d) + maxDist;

                    if (d >= 0)
                        houghSpace.getData()[ny][nx]++;
                }
            }
        }

        houghSpace = Histogram.normalize(houghSpace);
        houghSpace = Segmentation.smartMax(houghSpace, 40);

        StructElement elt = new StructElement(StructElement.Type.CIRCLE, 2);
        Image mask = Morphology.dilate(edges, elt);

        List<Line> lines = new ArrayList<Line>();
        for (int y = 0; y < houghSpace.getHeight(); y++) {
            for (int x = 0; x < houghSpace.getWidth(); x++) {
                if (houghSpace.get(x, y) > 0) {
                    Line l = new Line(x * (Math.PI / 180.), y - maxDist, img);
                    l.clip(mask);
                    lines.add(l);
                }
            }
        }
        return (lines);
    }
}
