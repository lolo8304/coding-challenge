package qr.generator;

import qr.Point2d;
import qr.Rect;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class QrCanvas {

    private static Map<Color, String> COLOR_MAP;

    static {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(Color.BLACK, "X");
        COLOR_MAP.put(Color.WHITE, "·");
        COLOR_MAP.put(Color.GREEN, "G");
        COLOR_MAP.put(Color.GRAY,  " ");
    }

    protected final Rect rect;


    public QrCanvas(Rect rect) {
        this.rect = rect;
    }

    // https://www.thonky.com/qr-code-tutorial/module-placement-matrix
    // print all modules into the Rectangle
    public void placeModules() {

    }

    public abstract void drawReq(Rect rect, boolean fill, Color... colors);

    public abstract void drawPoint(Point2d point, Color color);

    public abstract void drawStraightLine(Point2d point, int length, int thickness, Direction direction, Color... colors);

    protected String mapColor(Color color) {
        return COLOR_MAP.get(color);
    }

    public abstract void draw();

    public static enum Direction {
        HORIZONTAL,
        VERTICAL
    }
}
