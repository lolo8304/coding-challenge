package ocr.image.processing;

import ocr.image.Image;
import ocr.image.RGB;

public interface ImageProcessing {

    RGB[] convert(RGB[] pixels) throws Exception;
    default Image convert(Image image) throws Exception {
        return new Image(image.getWidth(), image.getHeight(), convert(image.getPixels()));
    }
}
