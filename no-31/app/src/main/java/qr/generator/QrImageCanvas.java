package qr.generator;

import qr.Point2d;
import qr.Rect;
import qr.Region;

import java.awt.*;

public class QrImageCanvas extends QrCanvas {
    public QrImageCanvas(Rect rect) {
        super(rect);
    }

    @Override
    public void drawReq(Rect rect, boolean fill, Color... colors) {

    }

    @Override
    public void drawPoint(Point2d point, Color color) {

    }

    @Override
    public void drawRegion(Region region, Color... colors) {

    }

    @Override
    public void drawStraightLine(Point2d point, int length, int thickness, Direction direction, Color... colors) {

    }

    @Override
    public boolean isEmpty(Point2d at) {
        return false;
    }

    @Override
    public void draw() {

    }
}
