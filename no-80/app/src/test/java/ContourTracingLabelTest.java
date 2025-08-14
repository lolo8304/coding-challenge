import ocr.OcrDetector;
import ocr.image.Image;
import ocr.image.RGB;
import ocr.image.SimpleRGBImage;
import ocr.image.labeling.ContourTracingLabeling;
import ocr.image.processing.*;
import ocr.image.labeling.Labels;
import org.junit.jupiter.api.Test;

public class ContourTracingLabelTest {

    private static final boolean SHOW_IMAGE = true;

    private Image fromBitMapText(String[] bitmapText) {
        int width = bitmapText[0].length();
        int height = bitmapText.length;
        var image = new Image(width, height);
        var pixels = image.getPixels();
        var i = 0;
        for (String line : bitmapText) {
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                if (c == 'X' || c == 'x' || c == '#' || c == 'b') {
                    pixels[i] = RGB.BLACK; // Assuming BLACK is defined as a constant in RGB
                } else if (c >= '2' && c <= '9') {
                    pixels[i] = Labels.toColor(c - '0'); // Convert character to label color
                } else {
                    pixels[i] = RGB.WHITE; // Assuming WHITE is defined as a constant in RGB
                }
                i++;
            }
        }
        return image;
    }

    public String[] toBitMapText(Image image) {
        String[] bitmapText = new String[image.getHeight()];
        var pixels = image.getPixels();
        for (int y = 0; y < image.getHeight(); y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < image.getWidth(); x++) {
                RGB pixel = pixels[y * image.getWidth() + x];
                if (pixel.equals(RGB.BLACK)) {
                    sb.append('X');
                } else if (pixel.equals(RGB.WHITE)) {
                    sb.append(' ');
                } else {
                    sb.append('0'); // Placeholder for other colors
                }
            }
            bitmapText[y] = sb.toString();
        }
        return bitmapText;
    }

    @Test void testContourTracingLabel() throws Exception {
        // Arrange
        var image = fromBitMapText(
                new String[]{
                        "---------",
                        "---XX----",
                        "-XXXXX---",
                        "-XX---XX-",
                        "--X----X-",
                        "--XXXXXX-",
                        "--XXXXXX-",
                        "--XXXXXX-",
                        "---------"
                }
        );
        // Act
        if (SHOW_IMAGE) OcrDetector.showImage(image);
        var labels = new Labels(image).detectLabels(true).detectLabels(false);
        image = labels.getImage();
        if (SHOW_IMAGE) OcrDetector.showImage(image);

        if (SHOW_IMAGE) while (true) { Thread.sleep(10000); }
    }

    @Test void testContourTracingLabel_2labels() throws Exception {
        // Arrange
        var image = fromBitMapText(
                new String[]{
                        "---------",
                        "-XXXX----",
                        "-X--X----",
                        "-X--X----",
                        "--XX--XXX",
                        "-----X--X",
                        "---XX--X-",
                        "---X----X",
                        "---XXXXX-"
                }
        );
        // Act
        if (SHOW_IMAGE) OcrDetector.showImage(image);
        var labels = new Labels(image).detectLabels(true).detectLabels(false);
        image = labels.getImage();
        if (SHOW_IMAGE) OcrDetector.showImage(image);

        if (SHOW_IMAGE) while (true) { Thread.sleep(10000); }
    }

    @Test void testContourTracingLabel1_4() throws Exception {
        // Arrange
        var image = fromBitMapText(
                new String[]{
                        "------------------------------------",
                        "-X---xxx-----xxx---xx---xx---xxxxx--",
                        "-X--x---x---x---x--xx---xx--X-----x-",
                        "-X------x---x---x--xx---xx--x-----x-",
                        "-X------x------x---xx---xx---xxxxx--",
                        "-X-----x------x----xxxxxxx----------",
                        "-X----x--------x--------xx---xxxxx--",
                        "-X---x------x---x-------xx--x-----x-",
                        "-X--x-------x---x-------xx--x-----x-",
                        "-X--xxxxx----xxx--------xx---xxxxx--",
                        "------------------------------------",
                        "------------------------------------",
                        "------------------------------------",
                        "------------------------------------",
                        "------------------------------------",
                        "------------------------------------",
                        "-X---xxx-----xxx---xx---xx---xxxxx--",
                        "-X--x---x---x---x--xx---xx--X-----x-",
                        "-X------x---x---x--xx---xx--x-----x-",
                        "-X------x------x---xx---xx--x-----x-",
                        "-X-----x------x----xxxxxxx--x-----x-",
                        "-X----x--------x--------xx--x-----x-",
                        "-X---x------x---x-------xx--x-----x-",
                        "-X--x-------x---x-------xx--x-----x-",
                        "-X--xxxxx----xxx--------xx---xxxxx--",
                        "------------------------------------",
                }
        );
        // Act
        if (SHOW_IMAGE) OcrDetector.showImage(image);
        var labeler = new ContourTracingLabeling();
        var labels = labeler.detectedContours(image);
        var boxes = labeler.detectedBoundingBoxes(labels);
        var words = labeler.detectedWords(boxes);
        image = labels.getImage();
        var originalImage = (Image)image.clone();
        for (var word : words) {
            var toShow = (Image)originalImage.clone();
            word.drawBoundingBox(toShow, RGB.PINK);
            if (SHOW_IMAGE) OcrDetector.showImage(toShow);
        }

        if (SHOW_IMAGE) while (true) { Thread.sleep(10000); }
    }

    @Test void testImageContourTracingLabel() throws Exception {
        // Arrange
        var image = new Image(SimpleRGBImage.loadRgbImage("../cc-test-image.png"));
        var pipelineSmall = new Pipeline()
                .addStep(new Scale(50));
        var origImage = pipelineSmall.process(image);
        var pipelineRaster = new Pipeline()
                .addStep(new ReplaceBackgroundUntilPercentage(75, RGB.RED))
                .addStep(new Scale(50))
                .addStep(new Raster(RGB.RED));
        image = pipelineRaster.process(image);
        var rasterImage = image;
        if (SHOW_IMAGE) OcrDetector.showImage(image, false);

        // Act
        var labeler = new ContourTracingLabeling();
        var labels = labeler.detectedContours(image);
        var boxes = labeler.detectedBoundingBoxes(labels);
        var words = labeler.detectedWords(boxes);
        image = labels.getImage();
        var toShow = (Image)rasterImage.clone();
        for (var word : words) {
            if (word.size() > 1) {
                word.drawBoundingBox(image, RGB.RED);
                if (SHOW_IMAGE) {
                    var thisToShow = (Image)toShow.clone();
                    word.drawBoundingBox(thisToShow, RGB.RED);
                    OcrDetector.showImage(thisToShow,false);
                }
            }
        }
        if (SHOW_IMAGE) while (true) { Thread.sleep(10000); }
    }
}
