package ocr;

import ocr.image.Image;
import ocr.image.RGB;
import ocr.image.SimpleRGBImage;
import ocr.image.labeling.ContourTracingLabeling;
import ocr.image.labeling.Labels;
import ocr.image.processing.*;

public class OcrDetector {
    private int width;
    private final boolean showImage;
    private final boolean showRasterImage;
    private final boolean showContours;

    public OcrDetector(int width, boolean showImage, boolean showRasterImage, boolean showContours) {
        this.width = width;
        this.showImage = showImage;
        this.showRasterImage = showRasterImage;
        this.showContours = showContours;
    }

    public static void showImage(Image image) throws Exception {
        showImage(image, true);
    }


    public static void showImage(Image image, boolean scaleGrid) throws Exception {
        var img = SimpleRGBImage.newRgbImage(
                new Pipeline()
                        .addStep(scaleGrid ? new ScaleGrid(1000) : new Skip())
                        .addStep(scaleGrid ? new Margin(20) : new Skip())
                        .process(image));
        img.show();
    }


    public String detectText(String imagePath) throws Exception {
        var image = new Image(SimpleRGBImage.loadRgbImage(imagePath));
        if (this.width == 0) {
            this.width = image.getWidth();
        }
        int scaleFactor = 100 * this.width / image.getWidth();
        var pipelineRaster = new Pipeline()
                .addStep(new ReplaceBackgroundUntilPercentage(75, RGB.RED))
                .addStep(new Scale(scaleFactor))
                .addStep(new Raster(RGB.RED));
        var processedImage = pipelineRaster.process(image);

        if (this.showImage) {
            showImage(new Pipeline().addStep(new Scale(scaleFactor)).process(image), false);
            if (this.showRasterImage) {
                showImage(processedImage, false);
            }
        }
        var labelingPipeline = new ContourTracingLabeling();
        var labels = labelingPipeline.detectedContours(processedImage);
        var boxes = labelingPipeline.detectedBoundingBoxes(labels);
        var words = labelingPipeline.detectedWords(boxes);
        if (this.showImage && this.showContours) {
            image = labels.getImage();
            for (var word : words) {
                word.drawBoundingBox(image, RGB.PINK);
            }
            showImage(image, false);
        }
        if (this.showImage) {
            while  (true) {
                Thread.sleep(10000); // Keep the image displayed
            }
        }
        return "no text detected";
    }
}
