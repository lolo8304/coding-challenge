package qr;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Region {

    public final List<Region> subregions;
    public Region parent;

    public final List<Rect> rectangles;

    public int level = 0;

    public Region(int x, int y, int width, int height) {
        this(new Rect(x, y, width, height));
    }
    public Region(Rect rect) {
        this();
        if (rect != null) {
            this.rectangles.add(rect);
        }
    }
    public Region() {
        this.parent = null;
        this.level = 0;
        this.subregions = new ArrayList<>();
        this.rectangles = new ArrayList<>();
    }

    public Region levelUp(Region region) {
        if (this.parent == null) {
            this.parent = new Region();
            this.parent.setLevel(this.level + 1);
            this.parent.subregions.add(region);
        }
        return this.parent;
    }

    public Region topLevel() {
        if (this.parent != null) {
            return this.parent.topLevel();
        } else {
            return this;
        }
    }

    public Region setLevel(int level) {
        this.level = level;
        return this;
    }

    public Region addRect(Rect rect) {
        this.rectangles.add(rect);
        return this;
    }
    public Region addRects(List<Rect> rects) {
        this.rectangles.addAll(rects);
        return this;
    }

    public void draw(Color color) {

    }

}
