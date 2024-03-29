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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        return ((Point2d) obj).x == this.x && ((Point2d) obj).y == this.y;
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

    public Point2d left() {
        return this.translate(-1, 0);
    }
    public Point2d right() {
        return this.translate(1, 0);
    }
    public Point2d up() {
        return this.translate(0, -1);
    }
    public Point2d down() {
        return this.translate(0, 1);
    }

    @Override
    public String toString() {
        return "Point2d(" + this.x + ", " + this.y + ")";
    }

}
