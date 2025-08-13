package ocr.image.processing;

import ocr.Ocr;
import ocr.image.RGB;
import ocr.image.SimpleRGBImage;

public class ReplaceBackgroundUntilPercentage implements ImageProcessing {

    private final int percentage;
    private final RGB color;

    public ReplaceBackgroundUntilPercentage(int percentage, RGB color) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        this.percentage = percentage;
        this.color = color;
    }

    private int percentageUnderThreshold(RGB[] pixels, int threshold) {
        int count = 0;
        for (RGB px : pixels) {
            if (px.r() <= threshold && px.g() <= threshold && px.b() <= threshold) {
                count++;
            }
        }
        return (count * 100) / pixels.length;
    }

    @Override
    public RGB[] convert(RGB[] pixels) throws Exception {
        var rgp = SimpleRGBImage.clone(pixels);
        var threshold = 2; // threshold for black
        var percentageUnderThreshold = this.percentageUnderThreshold(rgp, threshold);
        while (percentageUnderThreshold < percentage) {
            if (Ocr.verbose()) {
                System.out.println("Percentage under threshold (" + threshold + "): " + percentageUnderThreshold + "%");
            }
            rgp = SimpleRGBImage.clone(pixels);
            threshold += 1; // increase threshold
            var count = 0;
            for (int i = 0; i < rgp.length; i++) {
                if (rgp[i].r() <= threshold && rgp[i].g() <= threshold && rgp[i].b() <= threshold) {
                    // replace black with white
                    rgp[i] = color;
                    count++;

                }
            }
            percentageUnderThreshold = (count * 100) / pixels.length;
        }
        if (Ocr.verbose()) {
            System.out.println("Percentage under threshold (" + threshold + "): " + percentageUnderThreshold + "%");
        }
        return rgp;
    }
}
