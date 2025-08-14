package ocr.image.labeling;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ocr.image.Image;
import ocr.image.RGB;

import java.awt.*;

@Getter
public class OrientedChar {
    int label;
    double cx, cy, angle; // from Box
    int x, y, w, h;
    double xp, yp; // deskewed

    public void drawBoundingBox(Image image, RGB color) {
        var x1 = (int)(cx - w/2.0);
        var y1 = (int)(cy - h/2.0);
        var x2 = (int)(cx + w/2.0);
        var y2 = (int)(cy + h/2.0);
        var leftCornderPoint = new Point(x1, y1);
        var rightCornerPoint = new Point(x2, y2);
        // Draw the bounding box
        image.drawLine(leftCornderPoint.x, leftCornderPoint.y, rightCornerPoint.x, leftCornderPoint.y, color);
        image.drawLine(rightCornerPoint.x, leftCornderPoint.y, rightCornerPoint.x, rightCornerPoint.y, color);
        image.drawLine(rightCornerPoint.x, rightCornerPoint.y, leftCornderPoint.x, rightCornerPoint.y, color);
        image.drawLine(leftCornderPoint.x, rightCornerPoint.y, leftCornderPoint.x, leftCornderPoint.y, color);
    }
}

