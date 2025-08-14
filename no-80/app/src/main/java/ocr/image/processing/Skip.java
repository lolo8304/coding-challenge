package ocr.image.processing;

import ocr.image.Image;
import ocr.image.RGB;

import java.util.Arrays;

public class Skip implements ImageProcessing {

    public Skip() {

    }

    @Override
    public RGB[] convert(RGB[] pixels) throws Exception {
        return pixels;
    }

}
