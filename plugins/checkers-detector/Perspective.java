import java.lang.Math;

public class Perspective {
    public static void main(String[] args){
        System.out.println(getAngle(0,0, 7,0, 0,5));
    }

    /**
     * Gets the angle of a square triangle
     * x1 y1: The point situated at the square angle of the triangle
     * x2 y2: The point at the angle we want to get the value from
     * x3 y3: The third point.
     */
    public static double getAngle(int x1, int y1, int x2, int y2, int x3, int y3){
        double hypotenuse = getDistance(x2, y2, x3, y3);
        double opposite = getDistance(x3, y3, x1, y1);
        return Math.toDegrees(Math.asin(opposite / hypotenuse));
    }

    public static double getDistance(int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }
}
