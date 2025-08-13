package ocr.recognition;

import ocr.image.RGB;

public interface OcrRecognition {

    int[][] detectedContours(RGB[] pixels) throws Exception;

}
