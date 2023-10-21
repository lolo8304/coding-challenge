package qr;

import java.util.Arrays;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.Pair;

public enum ErrorCorrection {

    L(new int[][]{new int[]{4, 80, 1, 20, 80}}),
    M(new int[][]{new int[]{4, 64, 2, 18, 32}}),
    Q(new int[][]{new int[]{4, 48, 2, 26, 29}}),
    H(new int[][]{new int[]{4, 36, 4, 16, 9}});

    private final static int TOTAL_METADATA = 2;

    private final int[][] metadata;

    ErrorCorrection(int[][] metadata) {
        this.metadata = metadata;
    }

    public int[] metadata(int version) {
        for (int i = 0; i < this.metadata.length; i++) {
            var metaVersion = this.metadata[i][0];
            if (metaVersion == version) {
                return this.metadata[i];
            }
        }
        throw new IllegalArgumentException("Version "+version+" not configured" );
    }

    public int totalNumberOfCodeWords(int version) {
        return this.metadata(version)[1];
    }
    public int numberOfErrorCorrectionBlocks(int version) {
        return this.metadata(version)[2];
    }
    public int errorCorrectionCodeWordsPerBlock(int version) {
        return this.metadata(version)[3];
    }
    public int dataCodeWordsPerBlock(int version) {
        return this.metadata(version)[4];
    }

    private static int decode(int totalSymbols, int dataSymbols, String data) {
        int errorSymbols = totalSymbols - dataSymbols;
        return 0;
    }
}
