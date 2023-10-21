package qr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QrCode {
    private static final Pattern KANJI_PATTERN = Pattern.compile("[\\p{InCJK_Unified_Ideographs}]");
    private static final Pattern ALPHA_NUM_PATTERN = Pattern.compile("^[$%*+-./: a-zA-Z0-9]+$");
    private static final Pattern NUM_PATTERN = Pattern.compile("^[0-9]+$");

    private final String data;
    private final EncodingMode mode;

    public QrCode(String data) {
        this.data = data;
        this.mode = detectMode();
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
}
