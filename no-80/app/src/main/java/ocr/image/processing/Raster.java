package ocr.image.processing;

import ocr.image.Image;
import ocr.image.RGB;

import java.awt.*;

public class Raster implements ImageProcessing {

    private final RGB backgroundColor;

    public Raster(RGB backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private RGB invertColor(RGB color) {
        return new RGB(
            255 - color.r(),
            255 - color.g(),
            255 - color.b()
        );
    }

    @Override
    public RGB[] convert(RGB[] pixels) throws Exception {
        RGB[] rasteredPixels = new RGB[pixels.length];
        var white = RGB.white();
        var black = RGB.black();
        for (int i = 0; i < pixels.length; i++) {
            RGB px = pixels[i];
            if (px.r() == this.backgroundColor.r() && px.g() == this.backgroundColor.g() && px.b() == this.backgroundColor.b()) {
                rasteredPixels[i] = white;
            } else {
                rasteredPixels[i] = black;
            }
        }
        return rasteredPixels;
    }
}