package qr;

import qr.generator.QrCanvas;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Region {


    public final List<Rect> rectangles;

    public Region(int x, int y, int width, int height) {
        this(new Rect(x, y, width, height));
    }
    public Region(Rect rect) {
        this();
        if (rect != null) {
            this.rectangles.add(rect);
        }
    }
    public Region (Rect... rects) {
        this(Arrays.stream(rects).toList());
    }
    public Region (List<Rect> rects) {
        this();
        this.rectangles.addAll(rects);
    }
    public Region() {
        this.rectangles = new ArrayList<>();
    }

    public Region addRect(Rect rect) {
        this.rectangles.add(rect);
        return this;
    }

    public Region addRects(List<Rect> rects) {
        this.rectangles.addAll(rects);
        return this;
    }

    public void draw(QrCanvas canvas, String bits) {
        var colors = Arrays.stream(bits.split("")).toList().stream().map(y -> y.equals("0") ? Color.WHITE : Color.BLACK).toArray(Color[]::new);
        this.draw(canvas,colors);
    }

    public void draw(QrCanvas canvas, Color... colors) {
        var colorIndex = 0;
        var color = colors[colorIndex];
        var drawIndex = 0;
        var iterator = this.iterator();
        while (iterator.hasNext()) {
            var point = iterator.next();
            canvas.drawPoint(point, color);

            colorIndex = (colorIndex + 1) % colors.length;
            color = colors[colorIndex];
        }
    }

    public int size() {
        int sum = 0;
        for (var rect: this.rectangles) {
            sum += rect.size();
        }
        return sum;
    }

    public Iterator<Point2d> iterator() {
        return new RegionIndex(this);
    }

    public static class RegionIndex implements Iterator<Point2d> {
        private final Region region;
        private Rect currentRect;
        private Point2d nextPoint;

        private int rectIndex;
        private int index;
        private int relativeIndex;
        private int size;

        public RegionIndex(Region region) {
            this.region = region;
            this.reset();
        }

        public void reset() {
            this.index = 0;
            this.relativeIndex = 0;
            this.rectIndex = 0;
            this.currentRect = region.rectangles.size() > 0 ? region.rectangles.get(this.rectIndex) : null;
            this.nextPoint = this.currentRect != null ? this.currentRect.get(this.relativeIndex) : null;
            this.size = this.region.size();
        }

        @Override
        public Point2d next() {
            var currentPoint = this.nextPoint;
            if (this.currentRect == null) {
                throw new IllegalStateException("Region end reached");
            }
            if (this.relativeIndex < this.currentRect.size()-1) {
                // still some place
                this.relativeIndex++;
                this.index++;
                this.nextPoint = this.currentRect.get(this.relativeIndex);
            } else {
                // go to next rect
                this.rectIndex++;
                if (this.rectIndex < region.rectangles.size()) {
                    this.currentRect = this.region.rectangles.get(this.rectIndex);
                    this.relativeIndex = 0;
                    this.index++;
                    this.nextPoint = this.currentRect.get(this.relativeIndex);
                } else {
                    this.currentRect = null;
                    this.relativeIndex = 0;
                    this.index++;
                    this.nextPoint = null;
                }
            }
            return currentPoint;
        }

        @Override
        public boolean hasNext() {
            return this.currentRect != null;
        }


    }
}
