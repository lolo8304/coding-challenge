package ocr.image.labeling;

import ocr.image.Image;

import java.util.List;

public interface OcrRecognition {

    Labels detectedContours(Image image) throws Exception;
    BoundingBoxes detectedBoundingBoxes(Labels labels) throws Exception;
    List<OrientedWord> detectedWords(BoundingBoxes boxes) throws Exception;


    default BoundingBoxes detectedBoundingBoxes(Image image) throws Exception {
        return detectedBoundingBoxes(detectedContours(image));
    }
}
