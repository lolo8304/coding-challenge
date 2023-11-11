package qr.generator;

import qr.QrCode;

public class QrImageCanvasFactory extends QrCanvasFactory {

    private final int squareSize;

    public QrImageCanvasFactory(int squareSize) {

        this.squareSize = squareSize;
    }
    public QrCanvas newCanvasFromQrCode(QrCode qr) {
        return new QrImageCanvas(qr.version().modules().versionSize(), this.squareSize);
    }
}
