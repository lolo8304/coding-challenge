package qr.generator;

import qr.QrCode;

public class QrImageCanvasFactory extends QrCanvasFactory {

    public QrImageCanvasFactory() {

    }
    public QrCanvas newCanvasFromQrCode(QrCode qr) {
        return new QrImageCanvas(qr.version().modules().versionSize());
    }
}
