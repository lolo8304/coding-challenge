package qr;

import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;

import java.util.Arrays;
import java.util.regex.Pattern;

public class QrCode implements BitAppender {
    private static final Pattern KANJI_PATTERN = Pattern.compile("[\\p{InCJK_Unified_Ideographs}]");
    private static final Pattern ALPHA_NUM_PATTERN = Pattern.compile("^[$%*+-./: A-Z0-9]+$");
    private static final Pattern NUM_PATTERN = Pattern.compile("^[0-9]+$");

    private String data;
    private final EncodingMode mode;
    private final Quality quality;
    private Version version;

    private StringBuilder bits = new StringBuilder();
    private byte[][] groups;
    private byte[][] errorCorrectionCode;
    private int longestGroups = 0;

    public QrCode(String data) {
        this(data, Quality.M);
    }

    public QrCode(String data, Quality quality, EncodingMode mode) {
        this.data = data;
        this.mode = mode;
        if (mode == EncodingMode.ALPHA_NUMERIC) {
            this.data = data.toUpperCase();
        }
        this.quality = quality;
        this.version = Version.version(1);
    }

    public QrCode(String data, Quality quality) {
        this.data = data;
        this.mode = detectMode();
        if (mode == EncodingMode.ALPHA_NUMERIC) {
            this.data = data.toUpperCase();
        }
        this.quality = quality;
        this.version = Version.version(1);
    }

    private EncodingMode detectMode() {
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
    public Quality quality() {
        return this.quality;
    }

    public String encode() {
        this.calculateVersion();
        this.addExtraBitsAndFillUp();
        this.calculateGroups();
        this.calculateErrorCorrection();
        this.recalculateBits();
        return this.bits.toString();
    }

    // https://www.thonky.com/qr-code-tutorial/error-correction-coding#step-1-break-data-codewords-into-blocks-if-necessary
    private void calculateGroups() {
        var metadata = this.version.metaData(this.quality);
        this.groups = new byte[metadata.group1NumberOfBlocks + metadata.group2NumberOfBlocks][];
        var index = 0;
        var bytes = BitHelper.bitsToByteArray(bits.toString());
        for (int i = 0; i < metadata.group1NumberOfBlocks; i++) {
            this.groups[i] = Arrays.copyOfRange(bytes, index, index + metadata.group1NumberOfDataCodewordsInEachOfGroupBlocks);
            index += metadata.group1NumberOfDataCodewordsInEachOfGroupBlocks;
        }
        for (int i = 0; i < metadata.group2NumberOfBlocks; i++) {
            this.groups[metadata.group1NumberOfBlocks + i] = Arrays.copyOfRange(bytes, index, index + metadata.group2NumberOfDataCodewordsInEachOfGroupBlocks);
            index += metadata.group2NumberOfDataCodewordsInEachOfGroupBlocks;
        }
    }

    /*
    from
    https://github.com/kalaspuffar/qrcode-generator/blob/main/src/main/java/org/ea/DataEncoder.java
     */
    private void calculateErrorCorrection() {
        this.errorCorrectionCode = new byte[this.groups.length][];
        this.longestGroups = 0;
        int eccCodeWords = version.metaData(this.quality).codewordsPerBlock;
        for (int i = 0; i < this.groups.length; i++) {
            this.longestGroups = Math.max(this.longestGroups, this.groups[i].length);
            this.errorCorrectionCode[i] = generateECBytes(
                    this.groups[i],
                    eccCodeWords
            );
        }
    }

    /*
    https://github.com/kalaspuffar/qrcode-generator/blob/main/src/main/java/org/ea/DataEncoder.java
    still analyzing this code
     */
    private void recalculateBits() {
        this.bits = new StringBuilder();
        for (int i = 0; i < this.longestGroups; i++) {
            for (int j = 0; j < this.groups.length; j++) {
                if (this.groups[j].length > i) {
                    this.bits.append(BitHelper.byteToBits(groups[j][i]));
                }
            }
        }
        int eccCodeWords = version.metaData(this.quality).codewordsPerBlock;
        for (int i = 0; i < eccCodeWords; i++) {
            for (int j = 0; j < this.errorCorrectionCode.length; j++) {
                bits.append(BitHelper.byteToBits(this.errorCorrectionCode[j][i]));
            }
        }
        bits.append("00000000");
    }

    private void addExtraBitsAndFillUp() {
        var bitsCapacity = this.version.bitCapacity(this.quality);
        // add max 4 bits (0000) or until end of capacity
        int extraBitsOf0 = Math.min(bitsCapacity, this.bits.length() + 4);
        this.bits.append("0".repeat(Math.max(0, extraBitsOf0 - this.bits.length())));

        // fill next byte
        for (int i = 0; i < this.bits.length() % 8; i++) {
            this.bits.append("0");
        }

        // fill 236 / 17 until full
        var alternateFiller = 236;
        while (this.bits.length() < bitsCapacity) {
            this.bits.append(BitHelper.intToBits(alternateFiller));
            alternateFiller = alternateFiller == 236 ? 17 : 236;
        }
    }

    private void calculateVersion() {

        // check if bit size works with current version of QR code, if not expand to correct best one
        // count if bits depends on version - must be done twice
        var breakpointVersion = EncodingMode.getCurrentBreakPointVersion(this.version.version());
        this.setBitsAndVersion();
        if (this.version.version() > breakpointVersion) {
            setBitsAndVersion();
        }
    }

    private void setBitsAndVersion() {
        var oldVersion = this.version;
        this.bits = this.appendBits(new StringBuilder());
        var bitsCount = this.bits.length();
        this.version = Version.bestFixByBits(this.quality, bitsCount);
        if (oldVersion.version() != this.version.version()) {
            this.bits = this.appendBits(new StringBuilder());
        }
    }


    @Override
    public StringBuilder appendBits(StringBuilder builder) {
        this.mode
                .appendBits(builder)
                .append(BitHelper.intToBits(this.data.length(), this.mode.characterCountInBits(this.version)));
        switch (this.mode) {
            case ALPHA_NUMERIC -> builder.append(BitHelper.alphaNumericStringToBits(this.data));
            case NUMERIC -> builder.append(BitHelper.numericStringToBits(this.data));
            case KANJI -> builder.append(BitHelper.kanjiStringToBits(this.data));
            case BYTE -> builder.append(BitHelper.byteStringToBits(this.data));
        }
        return builder;
    }

    /*
    copied from https://github.com/kalaspuffar/qrcode-generator/blob/main/src/main/java/org/ea/DataEncoder.java
     */
    static byte[] generateECBytes(byte[] dataBytes, int numEcBytesInBlock) {
        int numDataBytes = dataBytes.length;
        int[] toEncode = new int[numDataBytes + numEcBytesInBlock];
        for (int i = 0; i < numDataBytes; i++) {
            toEncode[i] = dataBytes[i] & 0xFF;
        }

        new ReedSolomonEncoder(GenericGF.QR_CODE_FIELD_256).encode(toEncode, numEcBytesInBlock);

        byte[] ecBytes = new byte[numEcBytesInBlock];
        for (int i = 0; i < numEcBytesInBlock; i++) {
            ecBytes[i] = (byte) toEncode[numDataBytes + i];
        }
        return ecBytes;
    }

    public Version version() {
        return this.version;
    }
}
