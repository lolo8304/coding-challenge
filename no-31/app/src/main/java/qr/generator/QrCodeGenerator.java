package qr.generator;

import qr.Modules;
import qr.Point2d;
import qr.QrCode;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class QrCodeGenerator {

    private final QrCode qr;
    private QrCanvas canvas;
    private final Modules modules;
    private final Map<Integer, Mask> masks;
    private QrCodeGenerator bestGenerator;

    public QrCodeGenerator(QrCode qr) {
        this.qr = qr;
        this.modules = qr.version().modules();
        this.canvas = QrCanvasFactory.INSTANCE.newCanvasFromQrCode(qr);
        this.masks = new HashMap<>();
    }

    public QrCodeGenerator cloneForMasking() {
        var newGenerator = new QrCodeGenerator(this.qr);
        this.canvas.cloneTo(newGenerator.canvas);
        return newGenerator;
    }

    // https://www.thonky.com/qr-code-tutorial/module-placement-matrix
    // print all modules into the Rectangle
    public QrCodeGenerator drawBestGenerator() {
        drawFinderModules();
        drawSeparatorModules();
        drawTimingModules();
        drawAlignmentModules();
        drawDarkModule();
        drawReservedFormatInformation();
        drawReservedVersionInformation();
        drawBitStream();
        return drawBestMask();
    }


    // based on https://www.thonky.com/qr-code-tutorial/mask-patterns
    // return true if mask pattern hit
    private boolean maskPattern(Point2d pos, int mask) {
        var row = pos.x;
        var column = pos.y;
        return switch (mask) {
            case 0 -> ((row + column) % 2 == 0);
            case 1 -> row % 2 == 0;
            case 2 -> column % 3 == 0;
            case 3 -> (row + column) % 3 == 0;
            case 4 -> ((row / 2) + (column / 3)) % 2 == 0;
            case 5 -> ((row * column) % 2) + ((row * column) % 3) == 0;
            case 6 -> (((row * column) % 2) + ((row * column) % 3)) % 2 == 0;
            case 7 -> (((row + column) % 2) + ((row * column) % 3)) % 2 == 0;
            default -> false;
        };
    }

    private Mask createMaskForMe(int maskNo) {
        var mask = new Mask(this, maskNo);
        for (int x = 0; x < this.canvas.size().width(); x++) {
            for (int y = 0; y < this.canvas.size().height(); y++) {
                var point = new Point2d(x,y);
                if (maskPattern(point, mask.maskNo())) {
                    this.canvas.flipBitAt(point, Color.GREEN, Color.RED);
                    mask.incFlippedMask();
                }
            }
        }
        return mask;
    }


    private Mask createMaskFor(int maskNo) {
        return this.cloneForMasking().createMaskForMe(maskNo);
    }

    private QrCodeGenerator drawBestMask() {
        this.bestGenerator = this.getBestMask();
        return this.bestGenerator;
    }

    private QrCodeGenerator getBestMask() {
        this.canvas().draw();
        var minPenalties = Integer.MAX_VALUE;
        var bestMaskNo = -1;
        for (int maskNo = 0; maskNo < 8; maskNo++) {
            var mask = this.createMaskFor(maskNo);
            masks.put(maskNo, mask);
            var penalty = mask.evaluateConditions();
            if (penalty < minPenalties) {
                bestMaskNo = maskNo;
                minPenalties = penalty;
            }
        }
        System.out.println("Choose Mask no '"+bestMaskNo+"', pentalty = "+minPenalties);
        return masks.get(bestMaskNo).generator();
    }

    private void drawBitStream() {
        var bitIndex = 0;
        var bits = this.qr.bits();
        var iterator = new CanvasBitIterator(this);
        Color[] colors = { /* 0 */ Color.GREEN, /* 1 */ Color.RED };
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
            this.canvas.drawReq(rect, true, Color.BLUE);
        }
    }

    public QrCanvas canvas() {
        return this.canvas;
    }
    public QrCode qrCode() { return this.qr; }

    public Modules modules() { return this.modules;}
}
