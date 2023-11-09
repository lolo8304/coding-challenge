package qr.generator;

import qr.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class QrCodeGenerator {

    private final QrCode qr;
    private final QrCanvas canvas;
    private final Modules modules;
    private final Map<Integer, Masking> masks;
    private QrCodeGenerator bestGenerator;
    private int maskNo;

    public QrCodeGenerator(QrCode qr) {
        this.qr = qr;
        this.modules = qr.version().modules();
        this.canvas = QrCanvasFactory.INSTANCE.newCanvasFromQrCode(qr);
        this.masks = new HashMap<>();
        this.maskNo = -1;
    }

    public static QrCodeGenerator buildBestGenerator(QrCode qr) {
        var withMasking = true;
        if (withMasking) {
            return new QrCodeGenerator(qr).drawBestGenerator(withMasking).drawFinal();
        } else {
            return new QrCodeGenerator(qr).drawBestGenerator(withMasking);
        }
    }

    public QrCodeGenerator cloneForMasking() {
        var newGenerator = new QrCodeGenerator(this.qr);
        this.canvas.cloneTo(newGenerator.canvas);
        return newGenerator;
    }

    // https://www.thonky.com/qr-code-tutorial/module-placement-matrix
    // print all modules into the Rectangle
    public QrCodeGenerator drawBestGenerator(boolean withMasking) {
        drawFinderModules();
        drawSeparatorModules();
        drawTimingModules();
        drawAlignmentModules();
        drawDarkModule();

        drawReservedFormatInformation();
        drawReservedVersionInformation();
        drawBitStream();
        if (withMasking) {
            return drawBestMask();
        } else {
            return this;
        }
    }

    public QrCodeGenerator drawFinal() {
        this.drawFormatInformation();
        this.drawVersionInformation();
        this.drawWhite();
        return this;
    }

    private void drawWhite() {
        this.canvas.overwriteEnabled(); // allow to overwrite timing pattern.
        try {
            for (int i = 0; i < this.canvas.dimensions().size(); i++) {
                var point = this.canvas.getPoint(i);
                if (this.canvas.isWhite(point)) {
                    this.canvas.drawPoint(point,Color.WHITE);
                } else {
                    this.canvas.drawPoint(point,Color.BLACK);
                }
            }
        } finally {
            this.canvas.overwriteDisabled();
        }
    }


    // based on https://www.thonky.com/qr-code-tutorial/mask-patterns
    // return true if mask pattern hit
    private boolean maskPattern(Point2d pos, int mask) {
        var row = pos.y;
        var column = pos.x;
        switch (mask) {
            case 0: return (row + column) % 2 == 0;
            case 1: return row % 2 == 0;
            case 2: return column % 3 == 0;
            case 3: return (row + column) % 3 == 0;
            case 4: return (Math.floorDiv(row, 2) + Math.floorDiv(column, 3)) % 2 == 0;
            case 5: return (((row * column) % 2) + ((row * column) % 3)) == 0;
            case 6: return (((row * column) % 2) + ((row * column) % 3)) % 2 == 0;
            case 7: return (((row + column) % 2) + ((row * column) % 3)) % 2 == 0;
            default: return false;
        }
    }

    private Masking createMaskForMe(int maskNo) {
        var mask = new Masking(this, maskNo);
        for (int x = 0; x < this.canvas.dimensions().width(); x++) {
            for (int y = 0; y < this.canvas.dimensions().height(); y++) {
                var point = new Point2d(x,y);
                if (maskPattern(point, mask.maskNo())) {
                    if (this.canvas.flipBitAt(point, Color.GREEN, Color.PINK, Color.CYAN, Color.RED)) {
                        mask.incFlippedMask();
                    }
                }
            }
        }
        if (Qr.verbose2())
            System.out.println("Nof masks flipped "+mask.flippedMasks());
        return mask;
    }


    private Masking createMaskFor(int maskNo) {
        var mask = this.cloneForMasking().createMaskForMe(maskNo);

        // we need to add now per masked generator the version and format information.
        // the masking is based on this
        mask.generator().drawFormatInformation();
        mask.generator().drawVersionInformation();
        return mask;
    }

    private QrCodeGenerator drawBestMask() {
        this.bestGenerator = this.getBestMask();
        return this.bestGenerator;
    }

    private QrCodeGenerator getBestMask() {
        //this.canvas().draw();
        var minPenalties = Integer.MAX_VALUE;
        var bestMaskNo = -1;
        for (int maskNo = 0; maskNo < 8; maskNo++) {
            var mask = this.createMaskFor(maskNo);
            if (Qr.verbose3())
                mask.generator().canvas.draw();
            masks.put(maskNo, mask);
            var penalty = mask.evaluateConditions();
            if (penalty < minPenalties) {
                bestMaskNo = maskNo;
                minPenalties = penalty;
            }
        }
        if (Qr.verbose2())
            System.out.println("Choose Mask no '"+bestMaskNo+"', pentalty = "+minPenalties);
        return masks.get(bestMaskNo).generator();
    }

    private void drawBitStream() {
        var bitIndex = 0;
        var bits = this.qr.bits();
        var iterator = new CanvasBitIterator(this);
        Color[] colors = { /* 0 */ Color.GREEN, /* 1 */ Color.PINK };
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

    // fill https://www.thonky.com/qr-code-tutorial/format-version-information
    private void drawFormatInformation() {
        this.canvas.overwriteEnabled(); // allow to overwrite timing pattern.
        try {

            var mask = Mask.get(this.qr.quality(), this.maskNo);
            this.modules.reserveFormatInformation.draw(this.canvas, mask.informationBits());
        } finally {
            this.canvas.overwriteDisabled();
        }
    }

    private void drawReservedVersionInformation() {
        for (var rect : this.modules.reserveVersionInformation.rectangles) {
            this.canvas.drawReq(rect, true, Color.BLUE);
        }
    }

    // fill https://www.thonky.com/qr-code-tutorial/format-version-information
    private void drawVersionInformation() {
        this.canvas.overwriteEnabled(); // allow to overwrite timing pattern.
        try {
            this.modules.reserveVersionInformation.draw(this.canvas, BitHelper.reverseString(this.qr.version().informationBits()));
        } finally {
            this.canvas.overwriteDisabled();
        }
    }

    public QrCanvas canvas() {
        return this.canvas;
    }
    public QrCode qrCode() { return this.qr; }

    public Modules modules() { return this.modules;}

    public void mask(int maskNo) {
        this.maskNo = maskNo;
    }
}
