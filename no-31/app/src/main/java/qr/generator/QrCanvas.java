package qr.generator;

import qr.Point2d;
import qr.Rect;
import qr.Region;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class QrCanvas {

    private boolean overwrite;
    private boolean bw;
    private static Map<Color, String> COLOR_MAP;
    private static Map<Color, String> BW_COLOR_MAP;
    private static Map<Color, String> WB_COLOR_MAP;

    static {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(Color.BLACK,  "█");
        COLOR_MAP.put(Color.DARK_GRAY,  "X");

        COLOR_MAP.put(Color.WHITE,  "·");
        COLOR_MAP.put(Color.GREEN,  "G");
        COLOR_MAP.put(Color.BLUE,   "B");
        COLOR_MAP.put(Color.YELLOW, "Y");
        COLOR_MAP.put(Color.ORANGE, "O");
        COLOR_MAP.put(Color.GRAY,   " ");

        BW_COLOR_MAP = new HashMap<>();
        BW_COLOR_MAP.put(Color.BLACK,  "█");
        BW_COLOR_MAP.put(Color.DARK_GRAY,  "█");

        BW_COLOR_MAP.put(Color.WHITE,  "·");
        BW_COLOR_MAP.put(Color.GREEN,  "█");
        BW_COLOR_MAP.put(Color.BLUE,   "█");
        BW_COLOR_MAP.put(Color.YELLOW, "█");
        BW_COLOR_MAP.put(Color.ORANGE, "█");
        BW_COLOR_MAP.put(Color.GRAY,   " ");

        WB_COLOR_MAP = new HashMap<>();
        WB_COLOR_MAP.put(Color.BLACK,  "·");
        WB_COLOR_MAP.put(Color.DARK_GRAY,  "·");

        WB_COLOR_MAP.put(Color.WHITE,  "█");
        WB_COLOR_MAP.put(Color.GREEN,  "·");
        WB_COLOR_MAP.put(Color.BLUE,   "·");
        WB_COLOR_MAP.put(Color.YELLOW, "·");
        WB_COLOR_MAP.put(Color.ORANGE, "·");
        WB_COLOR_MAP.put(Color.GRAY,   " ");

    }

    protected final Rect rect;

    public void overwriteDisabled() {
        this.overwrite = false;
    }
    public void overwriteEnabled() {
        this.overwrite = true;
    }

    public boolean canOverwrite() {
        return this.overwrite;
    }

    public QrCanvas(Rect rect) {
        this.rect = rect;
        this.bw = true;
        if (this.bw) {
            //COLOR_MAP = BW_COLOR_MAP;
            COLOR_MAP = BW_COLOR_MAP;
        }
    }

    // https://www.thonky.com/qr-code-tutorial/module-placement-matrix
    // print all modules into the Rectangle
    public void placeModules() {

    }

    public abstract void drawReq(Rect rect, boolean fill, Color... colors);

    public abstract void drawPoint(Point2d point, Color color);

    public abstract void drawRegion(Region region, Color... colors);

    public abstract void drawStraightLine(Point2d point, int length, int thickness, Direction direction, Color... colors);

    public abstract boolean isEmpty(Point2d at);

    protected String mapColor(Color color) {
        return COLOR_MAP.get(color);
    }

    public abstract void draw();

    public void drawBitStream(Color... colors) {

    }

    public Rect size() {
        return this.rect;
    }

    public static enum Direction {
        HORIZONTAL,
        VERTICAL
    }
}
