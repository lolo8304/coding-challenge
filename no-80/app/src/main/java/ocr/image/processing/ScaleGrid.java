package ocr.image.processing;

import ocr.image.Image;
import ocr.image.RGB;

public class ScaleGrid implements ImageProcessing {

    private final int percentage;

    public ScaleGrid(int percentage) {
        if (percentage <= 0 || percentage > 1000) {
            throw new IllegalArgumentException("Percentage must be between 1 and 100");
        }
        this.percentage = percentage;
    }

    @Override
    public RGB[] convert(RGB[] pixels) throws Exception {
        throw new UnsupportedOperationException("Scale does not operate on pixel data directly, it operates on the Image object.");
    }

    private RGB[] convert(RGB[] pixels, int newWidth, int newHeight, int oldWidth, int oldHeight, int gridXCount, int gridYCount) {
        RGB[] scaledPixels = new RGB[newWidth * newHeight];
        double xRatio = (double) oldWidth / newWidth;
        double yRatio = (double) oldHeight / newHeight;

        var gridXSize = (int) newWidth / gridXCount;
        var gridYSize = (int) newHeight / gridYCount;

        for (int y = 0; y < newHeight; y++) {
            var isGridY = y == 0 || y == newHeight - 1 || (y % gridYSize == 0);
            for (int x = 0; x < newWidth; x++) {
                if (isGridY) {
                    scaledPixels[y * newWidth + x] = RGB.BLACK;
                } else {
                    var isGridX = x == 0 || x == newWidth - 1 || (x % gridXSize == 0);
                    int oldX = (int) (x * xRatio);
                    int oldY = (int) (y * yRatio);
                    if (isGridX) {
                        scaledPixels[y * newWidth + x] = RGB.BLACK;
                    } else {
                        scaledPixels[y * newWidth + x] = pixels[oldY * oldWidth + oldX];
                    }
                }
            }
        }
        return scaledPixels;
    }

    @Override
    public Image convert(Image image) throws Exception {
        int newWidth = (int) (image.getWidth() * (percentage / 100.0)) + image.getWidth() + 1;
        int newHeight = (int) (image.getHeight() * (percentage / 100.0)) + image.getHeight() + 1;
        RGB[] scaledPixels = convert(image.getPixels(), newWidth, newHeight, image.getWidth(), image.getHeight(), image.getWidth(), image.getHeight());
        return new Image(newWidth, newHeight, scaledPixels);
    }
}
