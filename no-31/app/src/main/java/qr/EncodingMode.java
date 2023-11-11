package qr;

public enum EncodingMode implements BitAppender {

    // https://www.thonky.com/qr-code-tutorial/data-encoding#versions-1-through-9

    NUMERIC (       "0001", new int[]{10, 12, 14}),
    ALPHA_NUMERIC ( "0010", new int[] {9, 11, 13}),
    BYTE(           "0100", new int[] {8, 16, 16}),
    KANJI(          "1000", new int[] {8, 10, 12});

    private static final int[] BREAK_POINTS = new int[] {0, 9, 26};

    private final int value;
    private final int[] characterCountInBits;


    EncodingMode(String bitString, int[] characterCountInBits) {
        this.value = BitConverter.bitsToInt(bitString);
        this.characterCountInBits = characterCountInBits;
    }

    public int value() {
        return this.value;
    }
    public int characterCountInBits(Version version) {
        return this.getBitLen(version.version());
    }

    @Override
    public StringBuilder appendBits(StringBuilder builder) {
        return builder
                .append(BitConverter.intToBits(this.value, 4));
    }

    public int getBitLen(int version) {
        if(version > BREAK_POINTS[2]) {
            return characterCountInBits[2];
        }
        if(version > BREAK_POINTS[1]) {
            return characterCountInBits[1];
        }
        return characterCountInBits[0];
    }

    public static int getCurrentBreakPointVersion(int version) {
        if(version < BREAK_POINTS[1]) {
            return BREAK_POINTS[1];
        }
        if(version < BREAK_POINTS[2]) {
            return BREAK_POINTS[2];
        }
        return 40;
    }
}
