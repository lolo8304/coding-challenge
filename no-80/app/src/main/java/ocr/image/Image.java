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

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new RGB[width * height];
    }

}
