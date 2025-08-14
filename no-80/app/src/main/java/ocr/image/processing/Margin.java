package ocr.image.processing;

import ocr.image.Image;
import ocr.image.RGB;

import java.util.Arrays;

public class Margin implements ImageProcessing {

    private final int margin;
    private final RGB fillColor;

    public Margin(int margin) {
        this(margin, new RGB(255, 255, 255)); // default to white fill color
    }

    public Margin(int margin, RGB fillColor) {
        this.fillColor = fillColor;
        if (margin < 0) {
            throw new IllegalArgumentException("margin must be >= 0");
        }
        this.margin = margin;
    }

    @Override
    public RGB[] convert(RGB[] pixels) throws Exception {
        throw new UnsupportedOperationException("Margin does not operate on pixel data directly, it operates on the Image object.");
    }

    private RGB[] convert(RGB[] pixels, int width, int height) {
        RGB[] scaledPixels = new RGB[ (width + 2 * margin) * (height + 2 * margin)];

        var newWidth = width + 2 * margin;
        Arrays.fill(scaledPixels, fillColor);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                scaledPixels[(y+margin) * newWidth + x + margin] = pixels[y * width + x];
            }
        }
        return scaledPixels;
    }

    @Override
    public Image convert(Image image) throws Exception {
        RGB[] scaledPixels = convert(image.getPixels(), image.getWidth(), image.getHeight());
        return new Image(image.getWidth() + 2 * margin, image.getHeight() + 2 * margin, scaledPixels);
    }
}
