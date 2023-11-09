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
    protected int quietZone;
    private Map<Color, String> colorMap;

    static {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(Color.BLACK,  "·");
        COLOR_MAP.put(Color.DARK_GRAY,  "·");

        COLOR_MAP.put(Color.WHITE,  "█"); // white
        COLOR_MAP.put(Color.BLUE,   "B"); // white

        COLOR_MAP.put(Color.YELLOW, "Y");
        COLOR_MAP.put(Color.ORANGE, "O");
        COLOR_MAP.put(Color.GRAY,   " ");

        COLOR_MAP.put(Color.RED,   "1");
        COLOR_MAP.put(Color.PINK,   "i");
        COLOR_MAP.put(Color.GREEN,  "0"); // white
        COLOR_MAP.put(Color.CYAN,  "o"); // white



        BW_COLOR_MAP = new HashMap<>();
        BW_COLOR_MAP.put(Color.BLACK,  "·");
        BW_COLOR_MAP.put(Color.DARK_GRAY,  "·");

        BW_COLOR_MAP.put(Color.WHITE,  "█"); // white
        BW_COLOR_MAP.put(Color.BLUE,   "·");
        BW_COLOR_MAP.put(Color.YELLOW, "·");
        BW_COLOR_MAP.put(Color.ORANGE, "·");
        BW_COLOR_MAP.put(Color.GRAY,   " ");

        BW_COLOR_MAP.put(Color.RED,   "1");
        BW_COLOR_MAP.put(Color.PINK,   "i");
        BW_COLOR_MAP.put(Color.GREEN,  "0"); // white
        BW_COLOR_MAP.put(Color.CYAN,  "o"); // white



        WB_COLOR_MAP = new HashMap<>();
        WB_COLOR_MAP.put(Color.BLACK,  "█");
        WB_COLOR_MAP.put(Color.DARK_GRAY,  "█");

        WB_COLOR_MAP.put(Color.WHITE,  "·"); // white
        WB_COLOR_MAP.put(Color.BLUE,   "█");
        WB_COLOR_MAP.put(Color.YELLOW, "█");
        WB_COLOR_MAP.put(Color.ORANGE, "█");
        WB_COLOR_MAP.put(Color.GRAY,   " ");

        WB_COLOR_MAP.put(Color.RED,   "1");
        WB_COLOR_MAP.put(Color.PINK,   "i");
        WB_COLOR_MAP.put(Color.GREEN,  "0"); // white
        WB_COLOR_MAP.put(Color.CYAN,  "o"); // white
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
        this.quietZone = 4;
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

    public String mapColor(Color color) {
        return COLOR_MAP.get(color);
    }

    public abstract void draw();

    public void draw(QrCanvas cv) {
        var point = new Point2d(0,0);

        // top quiet space
        for (int y = 0; y < this.quietZone; y++) {
            for (int x = 0; x < this.dimensions().width() + 2 * this.quietZone; x++) {
                cv.drawPoint(point, Color.WHITE);
                point = point.translate(1, 0);
            }
            point = new Point2d(0, point.y + 1);
        }

        for (int y = 0; y < this.rect.height(); y++) {

            for (int x = 0; x < this.quietZone; x++) {
                cv.drawPoint(point, Color.WHITE);
                point = point.translate(1, 0);
            }

            for (int x = 0; x < this.rect.width(); x++) {
                var realPoint = point.translate(-this.quietZone, -this.quietZone);
                //var color = this.isWhite(realPoint) ? Color.WHITE : Color.BLACK;
                var color = this.getColor(realPoint);
                cv.drawPoint(point, color);
                point = point.translate(1, 0);
            }

            for (int x = 0; x < this.quietZone; x++) {
                cv.drawPoint(point, Color.WHITE);
                point = point.translate(1, 0);
            }
            point = new Point2d(0, point.y + 1);
        }

        // bottom quiet space
        for (int y = 0; y < this.quietZone; y++) {
            for (int x = 0; x < this.dimensions().width() + 2 * this.quietZone; x++) {
                cv.drawPoint(point, Color.WHITE);
                point = point.translate(1, 0);
            }
            point = new Point2d(0, point.y + 1);
        }
    }

    public Rect dimensions() {
        return this.rect;
    }

    public void cloneTo(QrCanvas newCanvas) {
        newCanvas.colorMap = this.colorMap;
        newCanvas.bw = this.bw;
        newCanvas.rect = this.rect;
        newCanvas.overwrite = this.overwrite;
    }

    public abstract boolean flipBitAt(Point2d point, Color bit0Color, Color bit1Color, Color replBit0Color, Color replBit1Color);

    public abstract void finalize();

    public abstract boolean isWhite(Point2d point2d);
    public abstract Color getColor(Point2d point2d);
    public boolean isBlack(Point2d point2d) {
        return !this.isWhite(point2d);
    }

    public Point2d getPoint(int i) {
        return this.rect.get(i);
    }

    public abstract void saveFile(String outputFileName);


    public static enum Direction {
        HORIZONTAL,
        VERTICAL
    }
}
