package qr.generator;

import qr.QrCode;

public class QrTextCanvasFactory extends QrCanvasFactory {

    public QrTextCanvasFactory() {

    }
    public QrCanvas newCanvasFromQrCode(QrCode qr) {
        return new QrTextCanvas(qr.version().modules().versionSize());
    }
}
