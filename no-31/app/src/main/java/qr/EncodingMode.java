package qr;

public enum EncodingMode implements BitAppender {
    NUMERIC ("0001"),
    ALPHA_NUMERIC ("0010"),
    BYTE("0100"),
    KANJI("1000");

    private final int value;

    EncodingMode(String numberString) {
        this.value = Integer.parseInt(numberString, 2);
    }

    public int value() {
        return value;
    }

    @Override
    public StringBuilder appendBits(StringBuilder builder) {
        builder.append(Integer.toBinaryString(this.value));
        return builder;
    }
}
