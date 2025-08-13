package ocr.image.processing;

import ocr.image.Image;
import ocr.image.RGB;

public class Scale implements ImageProcessing {

    private final int percentage;

    public Scale(int percentage) {
        if (percentage <= 0 || percentage > 1000) {
            throw new IllegalArgumentException("Percentage must be between 1 and 100");
        }
        this.percentage = percentage;
    }

    @Override
    public RGB[] convert(RGB[] pixels) throws Exception {
        throw new UnsupportedOperationException("Scale does not operate on pixel data directly, it operates on the Image object.");
    }

    private RGB[] convert(RGB[] pixels, int newWidth, int newHeight, int oldWidth, int oldHeight) {
        RGB[] scaledPixels = new RGB[newWidth * newHeight];
        double xRatio = (double) oldWidth / newWidth;
        double yRatio = (double) oldHeight / newHeight;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int oldX = (int) (x * xRatio);
                int oldY = (int) (y * yRatio);
                scaledPixels[y * newWidth + x] = pixels[oldY * oldWidth + oldX];
            }
        }
        return scaledPixels;
    }

    @Override
    public Image convert(Image image) throws Exception {
        int newWidth = (int) (image.getWidth() * (percentage / 100.0));
        int newHeight = (int) (image.getHeight() * (percentage / 100.0));
        RGB[] scaledPixels = convert(image.getPixels(), newWidth, newHeight, image.getWidth(), image.getHeight());
        return new Image(newWidth, newHeight, scaledPixels);
    }
}
