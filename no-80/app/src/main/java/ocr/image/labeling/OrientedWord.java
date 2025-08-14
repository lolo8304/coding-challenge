package ocr.image.labeling;

import lombok.Getter;
import ocr.image.Image;
import ocr.image.RGB;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OrientedWord {
    private final List<OrientedChar> chars;

    public int x, y, width, height;

    public OrientedWord() {
        this.chars = new ArrayList<OrientedChar>();
    }
    public OrientedWord(List<OrientedChar> chars) {
        this.chars = chars;
        this.calculateTotalBoundingBox();
    }

    public OrientedWord add(OrientedChar orientedChar) {
        this.chars.add(orientedChar);
        this.calculateTotalBoundingBox();
        return this;
    }

    public int size() {
        return this.chars.size();
    }

    public void drawBoundingBoxes(Image image, RGB color) {
        for (OrientedChar orientedChar : chars) {
            orientedChar.drawBoundingBox(image, color);
        }
    }

    public void drawBoundingBox(Image image, RGB color) {
        if (chars.isEmpty()) return;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (OrientedChar orientedChar : chars) {
            int x = orientedChar.x;
            int y = orientedChar.y;
            int w = orientedChar.w;
            int h = orientedChar.h;

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x + w);
            maxY = Math.max(maxY, y + h);
        }
        image.drawLine(minX, minY, maxX, minY, color); // Top
        image.drawLine(maxX, minY, maxX, maxY, color); // Right
        image.drawLine(maxX, maxY, minX, maxY, color); // Bottom
        image.drawLine(minX, maxY, minX, minY, color); // Left
        this.x = minX;
        this.y = minY;
        this.width = maxX - minX;
        this.height = maxY - minY;
    }

    public void calculateTotalBoundingBox() {
        if (chars.isEmpty()) return;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (OrientedChar orientedChar : chars) {
            int x = orientedChar.x;
            int y = orientedChar.y;
            int w = orientedChar.w;
            int h = orientedChar.h;

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x + w);
            maxY = Math.max(maxY, y + h);
        }
        this.x = minX;
        this.y = minY;
        this.width = maxX - minX;
        this.height = maxY - minY;
    }
}
