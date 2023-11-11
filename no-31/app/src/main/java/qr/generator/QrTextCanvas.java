package qr.generator;

import qr.Point2d;
import qr.Rect;
import qr.Region;

import java.awt.*;
import java.util.Objects;

public class QrTextCanvas extends  QrCanvas {

    private final String[][] canvas;
    private boolean stretch;

    public QrTextCanvas(Rect rect) {
        super(rect);
        this.stretch = true;
        this.overwriteDisabled();
        this.canvas = new String[this.rect.width()][this.rect.height()];
        for (int i = rect.leftTop.x; i <= rect.rightBottom.x; i++) {
            for (int j = rect.leftTop.y; j <= rect.rightBottom.y; j++) {
                this.canvas[i][j] = mapColor(Color.GRAY);
            }
        }
    }
    public String mapColor(Color color) {
        var c = super.mapColor(color);
        return this.stretch ? c + c + c: c;
    }

    @Override
    public void drawReq(Rect rect, boolean fill, Color... colors) {
        if (fill && rect.width() > 1 && rect.height() > 1) {
            for (int i = rect.leftTop.x; i <= rect.rightBottom.x; i++) {
                for (int j = rect.leftTop.y; j <= rect.rightBottom.y; j++) {
                    this.drawPoint(new Point2d(i, j), colors[0]);
                }
            }
        } else if (!fill && rect.width() > 1 && rect.height() > 1) {
            // top line
            this.drawStraightLine(rect.leftTop, rect.width(), 1, Direction.HORIZONTAL, colors);
            // left down line
            this.drawStraightLine(rect.leftTop.translate(0,1), rect.height()-2, 1, Direction.VERTICAL, colors);
            // right down line
            this.drawStraightLine(new Point2d(rect.rightBottom.x, rect.leftTop.y).translate(0, 1), rect.height() - 2, 1, Direction.VERTICAL, colors);
            // bottom line
            this.drawStraightLine(new Point2d(rect.leftTop.x, rect.rightBottom.y), rect.width(), 1, Direction.HORIZONTAL, colors);
        } else if (rect.width() == 1 && rect.height() > 1) {
            this.drawStraightLine(rect.leftTop, rect.height(), 1, Direction.VERTICAL, colors);
        } else if (rect.height() == 1 && rect.width() > 1) {
            this.drawStraightLine(rect.leftTop, rect.width(), 1, Direction.HORIZONTAL, colors);
        } else if (rect.width() == 1 && rect.height() == 1){
            this.drawPoint(rect.leftTop, colors[0]);
        } else {
            // any height or widht == 0, nothing to draw
        }
    }

    @Override
    public boolean isEmpty(Point2d at) {
        return this.canvas[at.x][at.y].isBlank();
    }

    @Override
    public void drawPoint(Point2d point, Color color) {
        if (this.canOverwrite() || this.isEmpty(point)) {
            this.canvas[point.x][point.y] = mapColor(color);
        } else {
            throw new IllegalArgumentException("Point "+point+" has already a value '"+this.canvas[point.x][point.y]+"'");
        }
    }

    @Override
    public void cloneTo(QrCanvas newCanvas) {
        super.cloneTo(newCanvas);
        var textCanvas = (QrTextCanvas)newCanvas;
        for (int x = 0; x < textCanvas.canvas.length; x++) {
            System.arraycopy(this.canvas[x], 0, textCanvas.canvas[x], 0, textCanvas.canvas[x].length);
        }
        textCanvas.stretch = this.stretch;
    }

    @Override
    public boolean flipBitAt(Point2d point, Color bit0Color, Color bit1Color, Color replBit0Color, Color replBit1Color) {
        var equalBit0 = mapColor(bit0Color);
        var equalBit1 = mapColor(bit1Color);
        var mappedBit0 = mapColor(replBit0Color);
        var mappedBit1 = mapColor(replBit1Color);
        // if bit0 --> bit 1
        // if bit1 --> bit 0
        // if all else --> keep it
        var oldColor = this.canvas[point.x][point.y];
        var newColor = oldColor.equals(equalBit0) ?
                mappedBit1
                :
                (oldColor.equals(equalBit1) ?
                        mappedBit0
                        :
                        oldColor);
        if (Objects.equals(newColor, oldColor)) {
            return false;
        } else {
            this.canvas[point.x][point.y] = newColor;
            return true;
        }
    }

    @Override
    protected void finalizeDraw() {

    }

    @Override
    public boolean isWhite(Point2d point2d) {
        var color = this.canvas[point2d.x][point2d.y];
        return color.equals(this.mapColor(Color.WHITE))
                || color.equals(this.mapColor(Color.GREEN))
                || color.equals(this.mapColor(Color.BLUE))
                || color.equals(this.mapColor(Color.CYAN));
    }

    @Override
    public Color getColor(Point2d point2d) {
        var color = this.canvas[point2d.x][point2d.y];
        if (color.equals(this.mapColor(Color.WHITE))) return Color.WHITE;
        if (color.equals(this.mapColor(Color.BLACK))) return Color.BLACK;
        if (color.equals(this.mapColor(Color.BLUE))) return Color.BLUE;
        if (color.equals(this.mapColor(Color.GREEN))) return Color.GREEN;
        if (color.equals(this.mapColor(Color.RED))) return Color.RED;
        if (color.equals(this.mapColor(Color.DARK_GRAY))) return Color.DARK_GRAY;
        if (color.equals(this.mapColor(Color.YELLOW))) return Color.YELLOW;
        if (color.equals(this.mapColor(Color.ORANGE))) return Color.ORANGE;
        if (color.equals(this.mapColor(Color.GRAY))) return Color.GRAY;
        return Color.PINK;
    }

    @Override
    public void saveFile(String outputFileName) {

    }

    @Override
    public void drawRegion(Region region, Color... colors) {
        for (Rect rect: region.rectangles) {
            this.drawReq(rect,true, colors);
        }
    }

    @Override
    public void drawStraightLine(Point2d point, int length, int thickness, Direction direction, Color... colors) {
        Point2d point2 = null;
        int dx = 0;
        int dy = 0;
        if (direction == Direction.HORIZONTAL) {
            point2 = new Point2d(point.x + length - 1, point.y+thickness - 1);
            dx = 1;
            dy = 0;
        } else {
            point2 = new Point2d(point.x+thickness - 1, point.y + length - 1);
            dx = 0;
            dy = 1;
        }
        if (thickness == 1) {
            var pointToDraw = point;
            var colorIndex = 0;
            for (int i = 0; i < length; i++) {
                this.drawPoint(pointToDraw, colors[colorIndex]);

                pointToDraw = pointToDraw.translate(dx,dy);
                colorIndex = (colorIndex + 1) % colors.length;
            }
        } else {
            this.drawReq(new Rect(point,point2), true, colors[0]);
        }
    }

    @Override
    public void draw() {
        if (this.stretch) {
            System.out.println("Attention: "+mapColor(Color.BLACK)+" = 1 pixel ("+this.hashCode()+")");
        }
        System.out.println("------------------------------------------------------------------------------------------------------");
        var fullLengthWhiteBuilder = new StringBuilder();
        for (int i = 0; i < this.dimensions().width() + 2 * this.quietZone; i++) {
            fullLengthWhiteBuilder.append(this.mapColor(Color.WHITE));
        }
        var fullLengthWhite = fullLengthWhiteBuilder.toString();

        var length4WhiteBuilder = new StringBuilder();
        for (int i = 0; i < this.quietZone; i++) {
            length4WhiteBuilder.append(this.mapColor(Color.WHITE));
        }
        var length4White = length4WhiteBuilder.toString();

        for (int i = 0; i < 4; i++) {
            System.out.println(fullLengthWhite);
        }
        for (int y = 0; y < this.canvas.length; y++) {
            System.out.print(length4White);
            for (int x = 0; x < this.canvas.length; x++) {
                System.out.print(this.canvas[x][y]);
            }
            System.out.print(length4White);
            System.out.println();
        }
        for (int i = 0; i < 4; i++) {
            System.out.println(fullLengthWhite);
        }
    }
}
