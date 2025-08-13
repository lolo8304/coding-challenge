package ocr.image.processing;

import ocr.image.RGB;

public class MakeGrayscale implements ImageProcessing {

    public MakeGrayscale() {

    }

    @Override
    public RGB[] convert(RGB[] pixels) throws Exception {
        RGB[] grayPixels = new RGB[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            RGB px = pixels[i];
            int gray = (px.r() + px.g() + px.b()) / 3; // simple average
            grayPixels[i] = new RGB(gray, gray, gray);
        }
        return grayPixels;
    }
}
