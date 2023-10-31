package qr.generator;

import qr.Point2d;
import qr.Rect;
import qr.Region;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class QrCanvas {

    protected static Map<Color, String> COLOR_MAP;
    protected static Map<Color, String> BW_COLOR_MAP;
    protected static Map<Color, String> WB_COLOR_MAP;

    private boolean overwrite;
    private boolean bw;
    private Map<Color, String> colorMap;

    static {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(Color.BLACK,  "█");
        COLOR_MAP.put(Color.DARK_GRAY,  "█");

        COLOR_MAP.put(Color.WHITE,  "·");
        COLOR_MAP.put(Color.BLUE,   "B");
        COLOR_MAP.put(Color.YELLOW, "Y");
        COLOR_MAP.put(Color.ORANGE, "O");
        COLOR_MAP.put(Color.GRAY,   " ");

        COLOR_MAP.put(Color.RED,   "0");
        COLOR_MAP.put(Color.GREEN,  "1");



        BW_COLOR_MAP = new HashMap<>();
        BW_COLOR_MAP.put(Color.BLACK,  "█");
        BW_COLOR_MAP.put(Color.DARK_GRAY,  "█");

        BW_COLOR_MAP.put(Color.WHITE,  "·");
        BW_COLOR_MAP.put(Color.BLUE,   "█");
        BW_COLOR_MAP.put(Color.YELLOW, "█");
        BW_COLOR_MAP.put(Color.ORANGE, "█");
        BW_COLOR_MAP.put(Color.GRAY,   " ");

        BW_COLOR_MAP.put(Color.RED,   "0");
        BW_COLOR_MAP.put(Color.GREEN,  "1");




        WB_COLOR_MAP = new HashMap<>();
        WB_COLOR_MAP.put(Color.BLACK,  "·");
        WB_COLOR_MAP.put(Color.DARK_GRAY,  "·");

        WB_COLOR_MAP.put(Color.WHITE,  "█");
        WB_COLOR_MAP.put(Color.BLUE,   "·");
        WB_COLOR_MAP.put(Color.YELLOW, "·");
        WB_COLOR_MAP.put(Color.ORANGE, "·");
        WB_COLOR_MAP.put(Color.GRAY,   " ");

        WB_COLOR_MAP.put(Color.RED,   "0");
        WB_COLOR_MAP.put(Color.GREEN,  "1");
    }

    protected Rect rect;

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
        this(rect, false);
    }

    public QrCanvas(Rect rect, boolean useBlackAndWhite) {
        this.rect = rect;
        this.bw = useBlackAndWhite;
        if (this.bw) {
            //COLOR_MAP = BW_COLOR_MAP;
            this.colorMap = BW_COLOR_MAP;
        } else {
            this.colorMap = COLOR_MAP;
        }
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

    public Rect size() {
        return this.rect;
    }

    public void cloneTo(QrCanvas newCanvas) {
        newCanvas.colorMap = this.colorMap;
        newCanvas.bw = this.bw;
        newCanvas.rect = this.rect;
        newCanvas.overwrite = this.overwrite;
    }

    public abstract void flipBitAt(Point2d point, Color green, Color red);

    public abstract boolean isWhite(Point2d point2d);
    public boolean isBlack(Point2d point2d) {
        return !this.isWhite(point2d);
    }


    public static enum Direction {
        HORIZONTAL,
        VERTICAL
    }
}
