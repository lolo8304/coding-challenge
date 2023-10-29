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
        drawTimingModules();
        drawAlignmentModules();
        drawDarkModule();
        drawReservedFormatInformation();
        drawReservedVersionInformation();
        drawBitStream();
        this.canvas.draw();
    }

    private void drawBitStream() {
        var bitIndex = 0;
        var bits = this.qr.bits();
        var iterator = new CanvasBitIterator(this);
        Color[] colors = { Color.WHITE, Color.BLACK };
        while (iterator.hasNext()) {
            var pos = iterator.next();
            if (pos != null) {
                var bit = (int)bits.charAt(bitIndex++) - (int)'0';
                var color = colors[bit];
                this.canvas.drawPoint(pos, color);
                //this.canvas.draw();
            }
        }
    }

    private void drawFinderModules() {
        // center: 3,3 black fill
        // center: 4,4 white no fill
        // center: 5,5 black no fill
        for (var rect : this.modules.finderPatterns) {
            // outside black
            this.canvas.drawReq(rect, false, Color.DARK_GRAY);
            this.canvas.drawReq(rect.translate(1, 1,-2,-2), false, Color.WHITE);
            this.canvas.drawReq(rect.translate(2, 2,-4,-4), true, Color.DARK_GRAY);
        }
    }
    private void drawSeparatorModules() {
        for (var rect : this.modules.separatorPatterns) {
            this.canvas.drawReq(rect, true, Color.WHITE);
        }
    }
    private void drawTimingModules() {
        for (var rect : this.modules.timingPatterns) {
            this.canvas.drawReq(rect, true, Color.DARK_GRAY, Color.WHITE);
        }
    }

    private void drawDarkModule() {
        this.canvas.drawPoint(this.modules.darkModule, Color.DARK_GRAY);
    }

    private void drawAlignmentModules() {
        this.canvas.overwriteEnabled(); // allow to overwrite timing pattern.
        try {
            // 5 x 5 no fill, black
            // 3 x 3 no fill, white
            // 1 x 1 no fill, black
            for (var rect : this.modules.alignmentPatterns) {
                this.canvas.drawReq(rect, false, Color.DARK_GRAY);
                this.canvas.drawReq(rect.translate(1, 1, -2, -2), false, Color.WHITE);
                this.canvas.drawReq(rect.translate(2, 2, -4, -4), true, Color.DARK_GRAY);
            }
        } finally {
            this.canvas.overwriteDisabled();
        }
    }

    private void drawReservedFormatInformation() {
        for (var rect : this.modules.reserveFormatInformation.rectangles) {
            this.canvas.drawReq(rect, false, Color.BLUE);
        }
    }

    private void drawReservedVersionInformation() {
        for (var rect : this.modules.reserveVersionInformation) {
            this.canvas.drawReq(rect, true, Color.BLACK);
        }
    }

    public QrCanvas canvas() {
        return this.canvas;
    }
    public QrCode qrCode() { return this.qr; }

    public Modules modules() { return this.modules;}
}
