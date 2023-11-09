package qr.generator;

import qr.Point2d;
import qr.Qr;
import qr.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Masking {

    private final QrCodeGenerator generator;
    private final int maskNo;
    private int flippedMasks = 0;
    private final int[] penalties = new int[4];
    private final List<Block> condition2Blocks = new ArrayList<>();
    private int totalPenalties = 0;

    public Masking(QrCodeGenerator generator, int maskNo) {
        this.generator = generator;
        this.generator.mask(maskNo);
        this.maskNo = maskNo;
    }

    public int evaluateConditions() {
        this.penalties[0] = this.evaluateCondition1();
        this.penalties[1] = this.evaluateCondition2();
        this.penalties[2] = this.evaluateCondition3();
        this.penalties[3] = this.evaluateCondition4();
        this.totalPenalties = 0;
        for (var penalty: this.penalties) {
            this.totalPenalties += penalty;
        }

        if (Qr.verbose()) {
            System.out.println("Mask "+maskNo+" total   = "+this.totalPenalties);
        }
        return this.totalPenalties;
    }

    // https://www.thonky.com/qr-code-tutorial/data-masking#evaluation-condition-1
    public int evaluateCondition1() {
        var rect = this.generator.canvas().dimensions();
        var penalties = 0;

        for (int y = 0; y < rect.height(); y++) {
            var countConsecutive = 1;
            var pos = new Point2d(0, y);
            var lastIsWhite = this.generator.canvas().isWhite(pos);
            for (int x = 1; x < rect.width(); x++) {
                pos = new Point2d(x, y);
                var isWhite = this.generator.canvas().isWhite(pos);
                if (lastIsWhite == isWhite) {
                    countConsecutive++;
                } else {
                    if (countConsecutive >= 5) {
                        penalties = penalties + 3 + (countConsecutive - 5);
                        if (Qr.verbose2())
                            System.out.println("Found "+countConsecutive +" "+ (lastIsWhite ? "WHITE" : "BLACK") + " +"+(3 + (countConsecutive - 5)) +" lastpos="+pos.x+","+pos.y);
                    }
                    countConsecutive = 1;
                }
                lastIsWhite = isWhite;
            }
            if (countConsecutive >= 5) {
                penalties = penalties + 3 + (countConsecutive - 5);
                if (Qr.verbose2())
                    System.out.println("Found "+countConsecutive +" "+ (lastIsWhite ? "WHITE" : "BLACK") + " +"+(3 + (countConsecutive - 5)) +" lastpos="+pos.x+","+pos.y);
            }
        }
        if (Qr.verbose2())
            System.out.println(this.maskNo+".1 horizontal = "+penalties);
        for (int x = 0; x < rect.width(); x++) {
            var countConsecutive = 1;
            var pos = new Point2d(x, 0);
            var lastIsWhite = this.generator.canvas().isWhite(pos);
            for (int y = 1; y < rect.height(); y++) {
                pos = new Point2d(x, y);
                var isWhite = this.generator.canvas().isWhite(pos);
                if (lastIsWhite == isWhite) {
                    countConsecutive++;
                } else {
                    if (countConsecutive >= 5) {
                        penalties = penalties + 3 + (countConsecutive - 5);
                        if (Qr.verbose2())
                            System.out.println("Found "+countConsecutive +" "+ (lastIsWhite ? "WHITE" : "BLACK") + " +"+(3 + (countConsecutive - 5)) +" lastpos="+pos.x+","+pos.y);
                    }
                    countConsecutive = 1;
                }
                lastIsWhite = isWhite;
            }
            if (countConsecutive >= 5) {
                penalties = penalties + 3 + (countConsecutive - 5);
                if (Qr.verbose2())
                    System.out.println("Found "+countConsecutive +" "+ (lastIsWhite ? "WHITE" : "BLACK") + " +"+(3 + (countConsecutive - 5)) +" lastpos="+pos.x+","+pos.y);
            }
        }
        if (Qr.verbose1())
            System.out.println(this.maskNo+".1 = "+penalties);
        return penalties;
    }

    // https://www.thonky.com/qr-code-tutorial/data-masking#evaluation-condition-2
    public int evaluateCondition2_real() {
        var rect = this.generator.canvas().dimensions();
        var penalties = 0;

        var maxRect = this.generator.canvas().dimensions();
        var minRect = new Rect(0, 0, 2, 2);

        var currentBlock = new Block(this.generator.canvas(), minRect);
        var rootBlock = currentBlock;

        while (currentBlock.isInsideCanvas()) {
            while (currentBlock.isInsideCanvas()) {
                while (currentBlock.isSameColor()) {
                    var maxBlock = currentBlock.maximizeBlock();
                    condition2Blocks.add(maxBlock);
                    currentBlock = maxBlock.resize(2, 2).translateX(1);
                }
                currentBlock = currentBlock.translateX(1);
            }
            rootBlock = rootBlock.translateY(1);
            currentBlock = rootBlock;
        }
        // check if conditionBlocks are included in others
        var copyList = this.condition2Blocks.stream().map(Block::rect).collect(toList());
        copyList.forEach((x) -> {
            if (x.isIncludedInAny(copyList)) {
                this.condition2Blocks.removeIf(c -> c.rect() == x);
            }
        });

        for (var block : this.condition2Blocks) {
            penalties += (3 * (block.rect().width() - 1) * (block.rect().height() - 1));
        }
        if (Qr.verbose1())
            System.out.println(this.maskNo+".2 = "+penalties);

        return penalties;
    }

    // https://www.thonky.com/qr-code-tutorial/data-masking#evaluation-condition-2
    public int evaluateCondition2() {
        var rect = this.generator.canvas().dimensions();
        var penalties = 0;

        var maxRect = this.generator.canvas().dimensions();
        var minRect = new Rect(0, 0, 2, 2);

        var currentBlock = new Block(this.generator.canvas(), minRect);
        var rootBlock = currentBlock;

        while (currentBlock.isInsideCanvas()) {
            while (currentBlock.isInsideCanvas()) {
                if (currentBlock.isSameColor()) {
                    condition2Blocks.add(currentBlock);
                    penalties += 3;
                    if (Qr.verbose2())
                        System.out.println("Pos: "+currentBlock.rect().x()+","+currentBlock.rect().y());
                }
                currentBlock = currentBlock.translateX(1);
            }
            rootBlock = rootBlock.translateY(1);
            currentBlock = rootBlock;
        }
        if (Qr.verbose1())
            System.out.println(this.maskNo+".2 = "+penalties);

        return penalties;
    }


    // https://www.thonky.com/qr-code-tutorial/data-masking#evaluation-condition-3
    public int evaluateCondition3() {
        var rect = this.generator.canvas().dimensions();
        var penalties = 0;
        boolean[] compare1 = new boolean[]{false, true, false, false, false, true, false, true, true, true, true};
        boolean[] compare2 = new boolean[]{true, true, true, true, false, true, false, false, false, true, false};

        var root = new Block(this.generator.canvas(), new Rect(0,0, 11, 1) );
        var block = root;
        while (block.isInsideCanvas()) {
            while (block.isInsideCanvas()) {
                if (Arrays.equals(block.whitePattern(), compare1) || Arrays.equals(block.whitePattern(), compare2)) {
                    penalties += 40;
                }
                block = block.translateX(1);
            }
            root = root.translateY(1);
            block = root;
        }
        root = new Block(this.generator.canvas(), new Rect(0,0, 1, 11) );
        block = root;
        while (block.isInsideCanvas()) {
            while (block.isInsideCanvas()) {
                if (Arrays.equals(block.whitePattern(), compare1) || Arrays.equals(block.whitePattern(), compare2)) {
                    penalties += 40;
                }
                block = block.translateY(1);
            }
            root = root.translateX(1);
            block = root;
        }
        if (Qr.verbose1())
            System.out.println(this.maskNo+".3 = "+penalties);

        return penalties;
    }

    // https://www.thonky.com/qr-code-tutorial/data-masking#evaluation-condition-4
    public int evaluateCondition4() {
        var rect = this.generator.canvas().dimensions();
        var penalties = 0;
        var total = rect.width() * rect.height();
        var totalWhite = 0;
        var totalBlack = 0;
        for (int x = 0; x < rect.width(); x++) {
            for (int y = 0; y < rect.height(); y++) {
                if (this.generator.canvas().isWhite(new Point2d(x,y))) {
                    totalWhite++;
                } else {
                    totalBlack++;
                }
            }
        }
        if (Qr.verbose2())
            System.out.println("Total BLACK="+totalBlack+", WHITE="+totalWhite);

        var blackPercentage = Math.min(totalBlack,totalWhite) * 100.0 / Math.max(totalBlack, totalWhite);
        var blackMinPercentage = 5 * (int)Math.floor(blackPercentage / 5.0);
        var blackMaxPercentage = 5 * (int)Math.ceil(blackMinPercentage / 5.0);
        var blackMin50 = Math.abs(blackMinPercentage - 50);
        var blackMax50 = Math.abs(blackMaxPercentage - 50);
        var blackMinDiv5 = blackMin50 / 5;
        var blackMaxDiv5 = blackMax50 / 5;
        var min = Math.min(blackMinDiv5, blackMaxDiv5) * 10;
        if (Qr.verbose1())
            System.out.println(this.maskNo+".4 = "+min);
        return min;
    }


    public void incFlippedMask() {
        flippedMasks++;
    }

    public int flippedMasks() {
        return this.flippedMasks;
    }
    public int totalMasks() {
        var rect = this.generator.canvas().dimensions();
        return rect.width() * rect.height();
    }

    public int maskNo() {
        return this.maskNo;
    }

    public QrCodeGenerator generator() {
        return this.generator;
    }
}
