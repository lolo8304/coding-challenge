package qr.generator;

import qr.Modules;
import qr.QrCode;

import java.awt.*;

public class QrCodeGenerator {

    private final QrCode qr;
    private final QrCanvas canvas;
    private final Modules modules;

    public QrCodeGenerator(QrCode qr) {
        this.qr = qr;
        this.modules = qr.version().modules();
        this.canvas = QrCanvasFactory.INSTANCE.newCanvasFromQrCode(qr);
    }

    public void draw() {
        drawFinderModules();
        drawSeparatorModules();
        drawAlignmentModules();
        drawTimingModules();
        drawDarkModule();
        this.canvas.draw();
    }

    private void drawFinderModules() {
        // center: 3,3 black fill
        // center: 4,4 white no fill
        // center: 5,5 black no fill
        for (var rect : this.modules.finderPatterns) {
            // outside black
            this.canvas.drawReq(rect, false, Color.BLACK);
            this.canvas.drawReq(rect.translate(1, 1,-2,-2), false, Color.WHITE);
            this.canvas.drawReq(rect.translate(2, 2,-4,-4), true, Color.BLACK);
        }
    }
    private void drawSeparatorModules() {
        for (var rect : this.modules.separatorPatterns) {
            this.canvas.drawReq(rect, true, Color.WHITE);
        }
    }
    private void drawTimingModules() {
        for (var rect : this.modules.timingPatterns) {
            this.canvas.drawReq(rect, true, Color.BLACK, Color.WHITE);
        }
    }

    private void drawDarkModule() {
        this.canvas.drawPoint(this.modules.darkModule, Color.BLACK);
    }

    private void drawAlignmentModules() {
        // 5 x 5 no fill, black
        // 3 x 3 no fill, white
        // 1 x 1 no fill, black
        for (var rect : this.modules.alignmentPatterns) {
            this.canvas.drawReq(rect, false, Color.BLACK);
            this.canvas.drawReq(rect.translate(1, 1,-2,-2), false, Color.WHITE);
            this.canvas.drawReq(rect.translate(2, 2,-4,-4), true, Color.BLACK);
        }
    }

}
