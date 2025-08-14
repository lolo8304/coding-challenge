package ocr.image;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Image {
    private final RGB[] pixels;
    private final int width;
    private final int height;

    public Image(int width, int height, RGB[] pixels) {
        if (pixels.length != width * height) {
            throw new IllegalArgumentException("Pixels array size does not match width and height.");
        }
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }
    public Image(ImgBuffer img) {
        this.width = img.width();
        this.height = img.height();
        this.pixels = SimpleRGBImage.loadRgbPixels(img);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        RGB[] clonedPixels = new RGB[width * height];
        for (int i = 0; i < pixels.length; i++) {
            clonedPixels[i] = (RGB) pixels[i].clone();
        }
        return new Image(width, height, clonedPixels);
    }

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new RGB[width * height];
    }

    public void drawLine(int x1, int y1, int x2, int y2, RGB color) {
        var pixels = this.pixels;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        for (int err = dx - dy; x1 != x2 || y1 != y2;) {
            if (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) {
                pixels[y1 * width + x1] = color; // Draw black pixel
            }
            int e2 = err * 2;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

}
