import java.util.List;
import java.util.ArrayList;

/* This class contains static methods that will help finding groups of histograms that share similar properties.
 * The int[][][] arguments are a matrix of histograms, each histogram corresponding to a piece of the checkerboard.
 * Private classes are defined after the two main methods, minimalLink, which provides a minimal linkage classifiction and kmeans, which provides a kmeans classification.
 * As kmeans is ran multiple times, it is multithreaded.*/
public class Clusters {

    public static Image minimalLink (int[][][] arrays, int classes) {
        ArrayList<AHCNode> nodes = new ArrayList<AHCNode>();
        for (int y = 0; y < arrays.length; y++) {
            for (int x = 0; x < arrays[y].length; x++) {
                nodes.add(new AHCNode(arrays[y][x], x, y));
            }
        }
        int level = 0;
        while (nodes.size() > 1) {
            level++;
            AHCNode closestNode1 = nodes.get(0);
            AHCNode closestNode2 = nodes.get(1);
            double bestDistance = getDistance(closestNode1, closestNode2);
            for (AHCNode curNode1 : nodes) {
                for (AHCNode curNode2 : nodes) {
                    if (curNode1 == curNode2)
                        continue;
                    double distance = getDistance(curNode1, curNode2);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        closestNode1 = curNode1;
                        closestNode2 = curNode2;
                    }
                }
            }
            nodes.remove(closestNode1);
            nodes.remove(closestNode2);
            nodes.add(new AHCNode(closestNode1, closestNode2, level));
        }
        ArrayList<AHCNode> clusters = new ArrayList<AHCNode>();
        getClusters(clusters, nodes.get(0), classes);
        Image result = new Image(arrays[0].length, arrays.length);
        for (int i = 0; i < clusters.size(); i++) {
            nodes.clear();
            nodes.add(clusters.get(i));
            while (nodes.size() > 0) {
                AHCNode node = nodes.get(0);
                if (node.children[0] != null) {
                    nodes.add(node.children[0]);
                    nodes.add(node.children[1]);
                } else {
                    result.put(node.x, node.y, i * 255 / clusters.size());
                }
                nodes.remove(node);
            }
        }
        return result;
    }

    public static Image kmeans (int[][][] arrays, int classes) {
        KmeanThread[] threads = new KmeanThread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new KmeanThread(arrays, classes);
            threads[i].start();
        }

        ArrayList<Image> images = new ArrayList<Image>();
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
                int pixel = (int)threads[i].image.get(0, 0);
                boolean monochrome = true;
                for (int j = 0; monochrome && j < threads[i].image.getHeight(); j++) {
                    for (int k = 0; monochrome && k < threads[i].image.getWidth(); k++) {
                        monochrome = monochrome && pixel == threads[i].image.get(k, j);
                    }
                }
                if (!monochrome) {
                    boolean alreadyFound = false;
                    for (int j = 0; j < threads.length && !alreadyFound; j++) {
                        if (i == j) continue;
                        alreadyFound = threads[i].equals(threads[j]);
                    }
                    if (!alreadyFound) {
                        threads[i].image.display();
                        images.add(threads[i].image);
                    }
                    //threads[i].image.display();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int[][][] meanPixels = new int[arrays.length][arrays[0].length][classes];
        for (int i = 0; i < meanPixels.length; i++) {
            for (int j = 0; j < meanPixels.length; j++) {
                for (Image k : images){
                    meanPixels[i][j][(int)k.get(j, i)]++;
                }
            }
        }
        Image result = new Image(meanPixels[0].length, meanPixels.length);
        for (int i = 0; i < meanPixels.length; i++) {
            for (int j = 0; j < meanPixels.length; j++) {
                int max = meanPixels[i][j][0];
                for (int k = 0; k < meanPixels[i][j].length; k++) {
                    if (max < meanPixels[i][j][k])
                        max = meanPixels[i][j][k];
                }
                result.put(j, i, max * 255 / classes);
            }
        }
        return result;
    }

    private static class KmeanThread extends Thread {
        ArrayList<KmeanData> data;
        ArrayList<KmeanData> centers;
        Image image;
        boolean ran;
        public KmeanThread(int[][][] arrays, int classes) {
            ran = false;
            image = new Image(arrays[0].length, arrays.length);
            data = new ArrayList<KmeanData>();
            int maxVal = 0;
            for (int i = 0; i < arrays.length; i++) {
                for (int j = 0; j < arrays[0].length; j++) {
                    data.add(new KmeanData(arrays[i][j]));
                    maxVal = data.get(data.size() - 1).getMaxVal();
                }
            }
            centers = new ArrayList<KmeanData>();
            for (int i = 0; i < classes; i++) {
                centers.add(new KmeanData(new int[arrays[0][0].length], i));
                centers.get(centers.size() - 1).randomize(maxVal);
            }
        }

        public int hashCode() {
            return data.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (o == null || !(o instanceof KmeanThread))
                return false;

            KmeanThread otherThread = KmeanThread.class.cast(o);
            if (    !otherThread.ran || !this.ran ||
                    otherThread.centers.size() != this.centers.size() ||
                    otherThread.image.getWidth() != this.image.getWidth() ||
                    otherThread.image.getHeight() != this.image.getHeight()) {
                return false;
            }

            int[] correspondances = new int[255];
            for (int i = 0; i < correspondances.length; i++) {
                correspondances[i] = -1;
            }

            for (int j = 0; j < this.image.getHeight(); j++) { //check if both threads have the same pattern
                for (int i = 0; i < this.image.getWidth(); i++) {
                    if (correspondances[(int)this.image.get(i, j)] == -1) //Haven't found this cluster, save it
                        correspondances[(int)this.image.get(i, j)] = (int)otherThread.image.get(i, j);
                    else if (correspondances[(int)this.image.get(i, j)] != (int)otherThread.image.get(i, j))
                        return false;
                }
            }
            return true;
        }
        public void run() {
            boolean cont;
            do { //number of iterations of kmeans
                cont = false;
                for (KmeanData currentData : data) {
                    KmeanData nearestCluster = centers.get(0);
                    double smallestDistance = currentData.getDistanceTo(nearestCluster);
                    for (KmeanData currentCenter : centers) {
                        double distance = currentData.getDistanceTo(currentCenter);
                        if (distance < smallestDistance) {
                            smallestDistance = distance;
                            nearestCluster = currentCenter;
                        }
                    }
                    currentData.setCluster(nearestCluster.getCluster());
                }

                for (KmeanData currentCenter : centers) {
                    KmeanData newCenter = new KmeanData(new int[currentCenter.getData().length],
                            currentCenter.getCluster());
                    int dataCount = 0;
                    for(KmeanData currentData : data) {
                        if (currentData.getCluster() == newCenter.getCluster()) {
                            newCenter.add(currentData.getData());
                            dataCount++;
                        }
                    }
                    if (dataCount > 1) {
                        newCenter.divide(dataCount);
                    }
                    centers.set(centers.indexOf(currentCenter), newCenter);
                    cont = cont || currentCenter.getDistanceTo(newCenter) != 0.0;
                }
            } while (cont);
            for (int i = 0; i < data.size(); i++) {
                image.put(i / image.getWidth(), i % image.getHeight(), data.get(i).getCluster());
            }
            ran = true;
        }
    }

    private static class KmeanData {

        private int[] data;
        private int cluster;

        public KmeanData(int[] data) {
            this(data, -1);
        }

        public KmeanData(int[] data, int cluster) {
            this.data = data;
            this.cluster = cluster;
        }

        public double getDataMean() {
            int result = 0;
            for (int i = 0; i < this.data.length; i++) {
                result += this.data[i];
            }
            return result / this.data.length;
        }

        public int getMaxVal () {
            int result = 0;
            for (int i = 0; i < this.data.length; i++) {
                if(result < this.data[i]) {
                    result = this.data[i];
                }
            }
            return result;
        }

        public void divide (int n) {
            for (int i = 0; i < data.length; i++) {
                data[i] /= n;
            }
        }

        public void add (int n) {
            for (int i = 0; i < data.length; i++) {
                data[i] += n;
            }
        }

        public void divide (int[] array) {
            if (data.length != array.length){
                throw new IllegalArgumentException("Array isn't of the right size");
            }
            for (int i = 0; i < array.length; i++) {
                data[i] /= array[i];
            }
        }

        public void add (int[] array) {
            if (data.length != array.length){
                throw new IllegalArgumentException("Array isn't of the right size");
            }
            for (int i = 0; i < array.length; i++) {
                data[i] += array[i];
            }
        }

        public double getDistanceTo (KmeanData d) {
            return Clusters.getDistance(this.data, d.getData());
        }

        public void randomize () {
            this.randomize(1);
        }

        public void randomize (int i) {
            this.randomize(0, i);
        }

        public void randomize (int i, int j) {
            for (int k = 0; k < this.data.length; k++) {
                this.data[k] = (int)(Math.random() * j) + i;
            }
        }

        public void setCluster(int cluster) {
            this.cluster = cluster;
        }

        public int getCluster() {
             return this.cluster;
        }

        public int[] getData() {
            return this.data;
        }
    }

    private static class AHCNode {
        public double[] pos;
        public AHCNode[] children = new AHCNode[2];
        public int level, x, y;
        public AHCNode(int[] pos, int x, int y) {
            this.level = 0;
            this.x = x;
            this.y = y;
            this.pos = new double[pos.length];
            for (int i = 0; i < pos.length; i++) {
                this.pos[i] = (double)pos[i];
            }
        }
        public AHCNode(double[] pos, int x, int y) {
            this.level = 0;
            this.x = x;
            this.y = y;
            this.pos = pos;
        }
        public AHCNode(AHCNode n1, AHCNode n2, int level) {
            this.pos = getMean(n1, n2);
            this.level = level;
            this.children[0] = n1;
            this.children[1] = n2;
        }
    }

    private static double getDistance(AHCNode n1, AHCNode n2) {
        return getDistance(n1.pos, n2.pos);
    }

    private static double getDistance(double[] ar1, double[] ar2) {
        if(ar1.length != ar2.length){
            throw new IllegalArgumentException("Arrays aren't of the same length");
        }
        double result = 0;
        for (int i = 0; i < ar1.length; i++) {
            result += Math.pow(ar1[i] - ar2[i], 2);
        }
        return Math.sqrt(result);
    }

    private static double getDistance(int[] ar1, int[] ar2) {
        if(ar1.length != ar2.length){
            throw new IllegalArgumentException("Arrays aren't of the same length");
        }
        double result = 0;
        for (int i = 0; i < ar1.length; i++) {
            result += Math.pow(ar1[i] - ar2[i], 2);
        }
        return Math.sqrt(result);
    }

    private static double[] getMean(AHCNode n1, AHCNode n2) {
        return getMean(n1.pos, n2.pos);
    }

    private static double[] getMean(double[] a1, double[] a2) {
        double[] result = new double[a1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (a1[i] + a2[i]) / 2;
        }
        return result;
    }

    private static void getClusters(ArrayList<AHCNode> list, AHCNode node, int classNumber) {
        list.add(node);
        int iterator = 0;
        while (list.size() < classNumber && node != null) {
            if (node.children[0] != null) {
                list.remove(node);
                list.add(node.children[0]);
                list.add(node.children[1]);
            } else {
                iterator++;
            }
            node = list.get(iterator);
        }
    }


}
