package ocr.image.labeling;

import ocr.image.Image;

import java.util.List;

public class ContourTracingLabeling implements OcrRecognition {
    public ContourTracingLabeling() {
        // Constructor logic if needed
    }


    @Override
    public Labels detectedContours(Image image) throws Exception {
        return new Labels(image)
                .detectLabels(true)
                .detectLabels(false);
    }

    @Override
    public BoundingBoxes detectedBoundingBoxes(Labels labels) throws Exception {
        return new BoundingBoxes(labels).detectBoundingBoxes();
    }

    @Override
    public List<OrientedWord> detectedWords(BoundingBoxes boxes) throws Exception {
        return boxes.orderIntoLinesAndWords();
    }
}
