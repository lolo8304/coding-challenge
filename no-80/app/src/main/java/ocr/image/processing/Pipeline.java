package ocr.image.processing;

import ocr.image.Image;
import ocr.image.RGB;

import java.util.ArrayList;
import java.util.List;

public class Pipeline {
    private final List<ImageProcessing> steps;

    public Pipeline(List<ImageProcessing> steps) {
        this.steps = steps;
    }
    public Pipeline() {
        this.steps = new ArrayList<>();
    }

    public Pipeline addStep(ImageProcessing step) {
        this.steps.add(step);
        return this;
    }

    public RGB[] process(RGB[] pixels) throws Exception {
        RGB[] result = pixels;
        for (ImageProcessing step : steps) {
            result = step.convert(result);
        }
        return result;
    }

    public Image process(Image image) throws Exception {
        Image result = image;
        for (ImageProcessing step : steps) {
            result = step.convert(result);
        }
        return result;
    }
}
