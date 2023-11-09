package qr.generator;

import qr.Point2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CanvasBitIterator implements Iterable<Point2d>, Iterator<Point2d> {

    private final QrCodeGenerator generator;
    private final QrCanvas canvas;
    private final List<Point2d> nextPositions;
    private boolean up;

    public CanvasBitIterator(QrCodeGenerator generator) {
        this.generator = generator;
        this.canvas = this.generator.canvas();
        this.nextPositions = new ArrayList<>();
        this.nextPositions.add(this.canvas.dimensions().rightBottom);
        this.up = true;
    }

    @Override
    public boolean hasNext() {
        return !this.nextPositions.isEmpty();
    }

    @Override
    public Point2d next() {
        var pos = this.nextOnCanvas();
        while (pos != null && !this.canvas.isEmpty(pos)) {
            pos = this.nextOnCanvas();
        }
        return pos;
    }

    private boolean tryAddPosition(Point2d point) {
        if (this.canvas.dimensions().contains(point)) {
            this.nextPositions.add(point);
            return true;
        } else {
            return false;
        }
    }

    // https://www.thonky.com/qr-code-tutorial/module-placement-matrix#step-6-place-the-data-bits
    public Point2d nextOnCanvas() {
        if (this.nextPositions.isEmpty()) return null;
        var pos = this.nextPositions.remove(0);
        if (this.nextPositions.isEmpty()) { // fill again
            if (up) {
                // first go left
                var left = pos.left();
                if (!tryAddPosition(left)) return pos;
                var up = left.up().right();
                if (!this.canvas.dimensions().contains(up)) {
                    // now at the top. go to left again, turn flag and then down
                    var left2 = left.left();
                    // special condition - do not use vertical separator module go to next left
                    if (left2.x == this.generator.modules().timingPatterns.get(1).leftTop.x) {
                        left2 = left2.left();
                    }
                    if (!tryAddPosition(left2)) return pos;
                    this.up = false;
                } else {
                    if (!tryAddPosition(up)) return pos;
                }
            } else {
                var left = pos.left();
                if (!tryAddPosition(left)) return pos;
                var down = left.down().right();
                if (!this.canvas.dimensions().contains(down)) {
                    // now at the bottom. go to left again, turn flag and then up
                    var left2 = left.left();
                    if (!tryAddPosition(left2)) return pos;
                    this.up = true;
                } else {
                    if (!tryAddPosition(down)) return pos;
                }
            }
        }
        return pos;
    }

    @Override
    public Iterator<Point2d> iterator() {
        return this;
    }

}
