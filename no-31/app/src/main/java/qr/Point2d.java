package qr;

import java.util.ArrayList;
import java.util.List;

public class Point2d {

    public final int x;
    public final int y;

    public Point2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static List<Point2d> createMatrix(List<Integer> alignmentPatternPosition) {
        // build a matrix based on dimensions of alignment positions
        var list = new ArrayList<Point2d>();
        for (var xPos : alignmentPatternPosition) {
            for (var yPos : alignmentPatternPosition) {
                list.add(new Point2d(xPos, yPos));
            }
        }
        return list;
    }

    public Point2d translate (int dx, int dy) {
        return new Point2d(this.x + dx, this.y + dy);
    }

    @Override
    public String toString() {
        return "Point2d(" + this.x + ", " + this.y + ")";
    }

}
