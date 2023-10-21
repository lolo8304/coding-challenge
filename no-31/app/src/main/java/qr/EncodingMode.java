package qr;

public enum EncodingMode implements BitAppender {
    NUMERIC ("0001", 10, 1),
    ALPHA_NUMERIC ("0010", 9, 1),
    BYTE("0100", 8, 2),
    KANJI("1000", 8, 2);

    private final int value;
    private final int characterCountInBits;
    private final int stringBytes;

    EncodingMode(String bitString, int characterCountInBits, int stringBytes) {
        this.value = BitHelper.bitsToInt(bitString);
        this.characterCountInBits = characterCountInBits;
        this.stringBytes = stringBytes;
    }

    public int value() {
        return this.value;
    }
    public int characterCountInBits() {
        return this.characterCountInBits;
    }
    public int stringBytes() { return this.stringBytes; }

    @Override
    public StringBuilder appendBits(StringBuilder builder) {
        return builder
                .append(BitHelper.intToBits(this.value, 4));
    }
}
