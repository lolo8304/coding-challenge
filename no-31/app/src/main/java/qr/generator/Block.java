package qr.generator;

import qr.Point2d;
import qr.Rect;

import java.awt.Color;
import java.util.List;

public class Block {
    private final QrCanvas canvas;
    private Rect rect;
    private boolean isWhiteColor;
    private boolean isBlackColor;
    private boolean isOutsideCanvas;

    public Block(QrCanvas canvas, Rect rect) {
        this.canvas = canvas;
        this.rect = rect;
        this.isBlackColor = false;
        this.isWhiteColor = false;
        this.isOutsideCanvas = false;
        this.checkColors();
    }

    private Block checkColors() {
        var canvasRect = this.canvas.size();
        var intersection = this.rect.intersection(canvasRect);
        if (intersection.isEmpty() || !intersection.get().equals(this.rect)) {
            this.isOutsideCanvas = true;
            return this;
        }
        this.isWhiteColor = true;
        this.isBlackColor = true;
        for (int y = this.rect.y(); y <= this.rect.rightBottom.y; y++) {
            for (int x = this.rect.x(); x <= this.rect.rightBottom.x; x++) {
                var isPosWhite = this.canvas.isWhite(new Point2d(x,y));
                this.isWhiteColor = this.isWhiteColor && isPosWhite;
                this.isBlackColor = this.isBlackColor && !isPosWhite;
            }
        }
        return this;
    }

    public Rect rect() {
        return this.rect;
    }
    public boolean isSameColor() {
        return this.isBlackColor || this.isWhiteColor;
    }

    public boolean isOutsideCanvas() {
        return this.isOutsideCanvas;
    }
    public boolean isInsideCanvas() {
        return !this.isOutsideCanvas;
    }

    public Block maximizeBlock() {
        var maxBlock = this;
        while (maxBlock.isSameColor()) {
            var dW = maxBlock.translateW(1);
            if (dW.isSameColor()) {
                maxBlock = dW;
            }
            var dH = maxBlock.translateH(1);
            if (dH.isSameColor()) {
                maxBlock = dH;
            }
            if (!dW.isSameColor() && !dH.isSameColor()) {
                return maxBlock;
            }
        }
        return maxBlock;
    }

    public Block translateW(int dw) {
        return this.translate(0, 0, dw, 0);
    }
    public Block translateH(int dh) {
        return this.translate(0, 0, 0, dh);
    }
    public Block translateX(int dx) {
        return this.translate(dx, 0, 0, 0);
    }
    public Block translateY(int dy) {
        return this.translate(0, dy, 0, 0);
    }

    public Block resize(int w, int h) {
        return new Block(this.canvas, new Rect(this.rect.leftTop, w, h));
    }

    public Block translate(int dx, int dy, int dw, int dh) {
        return new Block(this.canvas, this.rect.translate(dx, dy, dw, dh)).checkColors();
    }

    public boolean[] whitePattern() {
        if (this.rect.width() != 1 && this.rect.height() != 1) {
            throw new IllegalArgumentException("White pattern can only calculated on a width or height = 1" );
        }
        if (this.rect.height() == 1) {
            var result = new boolean[this.rect.width()];
            var pos = this.rect.leftTop;
            for (int i = 0; i < this.rect.width(); i++) {
                result[i] = this.canvas.isWhite(pos);
                pos = pos.translate(1, 0);
            }
            return result;
        } else {
            var result = new boolean[this.rect.height()];
            var pos = this.rect.leftTop;
            for (int i = 0; i < this.rect.height(); i++) {
                result[i] = this.canvas.isWhite(pos);
                pos = pos.translate(0, 1);
            }
            return result;
        }
    }


    @Override
    public String toString() {
        return "Block[ "+this.rect+" W?="+this.isWhiteColor+", B?="+this.isBlackColor+" ]";
    }
}
