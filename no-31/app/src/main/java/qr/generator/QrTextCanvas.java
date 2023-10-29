package qr.generator;

import qr.Point2d;
import qr.Rect;
import qr.Region;

import java.awt.*;

public class QrTextCanvas extends  QrCanvas {

    private final String[][] canvas;
    private final boolean stretch;

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
    protected String mapColor(Color color) {
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
            System.out.println("Attention: "+mapColor(Color.BLACK)+" = 1 pixel");
        }
        System.out.println("------------------------------------------------------------------------------------------------------");
        for (int y = 0; y < this.canvas.length; y++) {
            for (int x = 0; x < this.canvas.length; x++) {
                System.out.print(this.canvas[x][y]);
            }
            System.out.println();
        }
    }


}
