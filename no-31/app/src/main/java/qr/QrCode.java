package qr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QrCode implements BitAppender {
    private static final Pattern KANJI_PATTERN = Pattern.compile("[\\p{InCJK_Unified_Ideographs}]");
    private static final Pattern ALPHA_NUM_PATTERN = Pattern.compile("^[$%*+-./: A-Z0-9]+$");
    private static final Pattern NUM_PATTERN = Pattern.compile("^[0-9]+$");

    private final String data;
    private final EncodingMode mode;
    private final ErrorCorrection correction;
    private final int version;

    public QrCode(String data) {
        this.data = data.toUpperCase();
        this.mode = detectMode();
        this.correction = ErrorCorrection.M;
        this.version = 4;
    }

    public EncodingMode detectMode() {
        if (this.containsNumeric()) return EncodingMode.NUMERIC;
        if (this.containsAlphaNumeric()) return EncodingMode.ALPHA_NUMERIC;
        if (this.containsKanji()) return EncodingMode.KANJI;
        return  EncodingMode.BYTE;
    }

    private boolean containsKanji() {
        return KANJI_PATTERN.matcher(this.data).find();
    }
    private boolean containsAlphaNumeric() {
        return ALPHA_NUM_PATTERN.matcher(this.data).find();
    }

    private boolean containsNumeric() {
        return NUM_PATTERN.matcher(this.data).find();
    }

    public EncodingMode mode() {
        return this.mode;
    }

    @Override
    public StringBuilder appendBits(StringBuilder builder) {
        this.mode
                .appendBits(builder)
                .append(BitHelper.intToBits(this.data.length(), this.mode.characterCountInBits()));
        switch (this.mode) {
            case ALPHA_NUMERIC -> builder.append(BitHelper.alphaNumericStringToBits(this.data));
            case NUMERIC -> builder.append(BitHelper.numericStringToBits(this.data));
            case KANJI -> throw new IllegalArgumentException("Not implemented yet");
            case BYTE -> builder.append(BitHelper.byteStringToBits(this.data));

        }
        if (this.mode == EncodingMode.ALPHA_NUMERIC) {
        } else {
            builder.append(BitHelper.stringToBits(this.data));
        }
        return builder;
    }
}
