package qr.generator;

import qr.QrCode;

public abstract class QrCanvasFactory {

    public static QrCanvasFactory INSTANCE = new QrTextCanvasFactory();

    public QrCanvasFactory() {
    }

    public abstract QrCanvas newCanvasFromQrCode(QrCode qr);
}
