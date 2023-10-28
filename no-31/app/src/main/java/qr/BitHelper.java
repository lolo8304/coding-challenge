package qr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BitHelper {

    public static String intToBits(int value) {
        return intToBits(value, 0);
    }
    public static String intToBits(int value, int nofBits) {
        String bitString =  Integer.toBinaryString(value);
        if (nofBits == 0) {
            if (bitString.length() % 4 != 0) {
                var builder = new StringBuilder(bitString);
                while (builder.length() % 4 != 0) {
                    builder.insert(0, "0");
                }
                return builder.toString();
            } else {
                return bitString;
            }
        } else {
            if (bitString.length() < nofBits) {
                var builder = new StringBuilder(bitString);
                while (builder.length() < nofBits) {
                    builder.insert(0, "0");
                }
                return builder.toString();
            } else {
                return bitString;
            }
        }
    }

    public static String uintToBits(long value, int nofBits) {
        String bitString =  Long.toBinaryString(value);
        if (nofBits == 0) {
            if (bitString.length() % 4 != 0) {
                var builder = new StringBuilder(bitString);
                while (builder.length() % 4 != 0) {
                    builder.insert(0, "0");
                }
                return builder.toString();
            } else {
                return bitString;
            }
        } else {
            if (bitString.length() < nofBits) {
                var builder = new StringBuilder(bitString);
                while (builder.length() < nofBits) {
                    builder.insert(0, "0");
                }
                return builder.toString();
            } else if (bitString.length() > nofBits) {
                throw new IllegalArgumentException("int is too big for requested bits ("+nofBits+")");
            } else {
                return bitString;
            }
        }
    }

    public static int bitsToInt(String value) {
        return Integer.parseUnsignedInt(value, 2);
    }
    public static byte bitsToByte(String value) {
        return (byte)Integer.parseInt(value, 2);
    }
    public static byte[] bitsToByteArray(String value) {
        var bytes = new byte[value.length() / 8];
        var bytesIndex = 0;
        for (int i = 0; i < value.length();) {
            bytes[bytesIndex] = bitsToByte(value.substring(i, i + 8));
            i += 8;
            bytesIndex++;
        }
        return bytes;
    }

    public static String bitsToString(String value) {
        if (value.length() % 8 != 0) {
            throw new IllegalArgumentException("Invalid bit string length (" + value.length() + ")");
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i += 8) {
            String byteString = value.substring(i, i + 8);
            int byteValue = Integer.parseInt(byteString, 2);
            char character = (char) byteValue;
            result.append(character);
        }
        return result.toString();
    }

    public static String stringToBits(String value) {
        return stringToBits(value, 0);
    }

    public static String stringToBits(String value, int nofBits) {
        var bitString = new StringBuilder();
        for (char c : value.toCharArray()) {
            StringBuilder binaryChar = new StringBuilder(Integer.toBinaryString(c));
            while (binaryChar.length() < 8) {
                binaryChar.insert(0, "0");
            }
            bitString.append(binaryChar);
        }
        if (nofBits == 0) {
            return bitString.toString();
        } else {
            if (bitString.length() < nofBits) {
                var builder = new StringBuilder(bitString);
                while (builder.length() < nofBits) {
                    builder.insert(0, "0");
                }
                return builder.toString();
            } else if (bitString.length() > nofBits) {
                throw new IllegalArgumentException("int is too big for requested bits ("+nofBits+")");
            } else {
                return bitString.toString();
            }
        }
    }

    static {
        initializeAlphaNumericTable();
    }

    private static Character[] ALPHA_ENCODING_TO_CHAR;
    private static Map<Character,Integer> ALPHA_ENCODING_TO_NUMBER;

    private static void initializeAlphaNumericTable() {
        var stream = BitHelper.class.getResourceAsStream("/alphanumeric-map.txt");
        if (stream != null) {
            var reader = new BufferedReader(new InputStreamReader(stream));
            var lines = reader.lines().toList();
            ALPHA_ENCODING_TO_CHAR = new Character[(int)lines.size()];
            ALPHA_ENCODING_TO_NUMBER = new HashMap<>();
            lines.forEach((x) -> {
                var splitted = x.split("=");
                var index = Integer.parseInt(splitted[1]);
                var ch = splitted[0].charAt(0);
                ALPHA_ENCODING_TO_CHAR[index] = ch;
                ALPHA_ENCODING_TO_NUMBER.put(ch, index);
            });
        }
    }

    // https://www.thonky.com/qr-code-tutorial/alphanumeric-mode-encoding
    public static String alphaNumericStringToBits(String value) {
        value = value.toUpperCase();
        // own alphabet, combined 2 chars together char1 * 45 + char2
        // convert to 11bit sequences
        // if odd number, use 6bit sequence for last char
        var bitString = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            var ch1 = ALPHA_ENCODING_TO_NUMBER.get(value.charAt(i));
            i++;
            if (i < value.length()) {
                var ch2 = ALPHA_ENCODING_TO_NUMBER.get(value.charAt(i));
                StringBuilder binaryChar = new StringBuilder(Integer.toBinaryString(45 * ch1 + ch2));
                while (binaryChar.length() < 11) {
                    binaryChar.insert(0, "0");
                }
                bitString.append(binaryChar);
            } else {
                StringBuilder binaryChar = new StringBuilder(Integer.toBinaryString(ch1));
                while (binaryChar.length() < 6) {
                    binaryChar.insert(0, "0");
                }
                bitString.append(binaryChar);
            }
        }
        return bitString.toString();
    }

    public static String bitsToAlphaNumericString(String value) {
        var builder = new StringBuilder();
        var i = 0;
        while (i < value.length()) {
            if (value.length() - i == 6) {
                var bits6 = value.substring(i, i+6);
                i += 6;
            } else {
                var bits11 = value.substring(i, i+11);
                var charCombined = Integer.parseUnsignedInt(bits11, 2);
                var ch1 = charCombined / 45;
                var ch2 = charCombined % 45;
                builder.append(ALPHA_ENCODING_TO_CHAR[ch1]).append(ALPHA_ENCODING_TO_CHAR[ch2]);
                i += 11;
            }
        }
        return builder.toString();
    }


    // https://www.thonky.com/qr-code-tutorial/numeric-mode-encoding
    public static String numericStringToBits(String value) {
        value = value.toUpperCase();
        // split string by 3 digits, at the end only 1 or 2
        // encode each number into 10 bits
        // 2 converted into 7 bits
        // 1 converted into 4 bits
        var builder = new StringBuilder();
        var i = 0;
        while (i < value.length()) {
            if (value.length() - i == 1) {
                var number1 = value.substring(i, i + 1);
                var bits = Integer.toBinaryString(Integer.parseInt(number1));
                StringBuilder binaryChar = new StringBuilder(bits);
                while (binaryChar.length() < 4) {
                    binaryChar.insert(0, "0");
                }
                builder.append(binaryChar);
                i += 1;
            } else if (value.length() - i == 2) {
                var number2 = value.substring(i, i + 2);
                var bits = Integer.toBinaryString(Integer.parseInt(number2));
                StringBuilder binaryChar = new StringBuilder(bits);
                while (binaryChar.length() < 7) {
                    binaryChar.insert(0, "0");
                }
                builder.append(binaryChar);
                i += 2;
            } else {
                var number3 = value.substring(i, i+3);
                var bits = Integer.toBinaryString(Integer.parseInt(number3));
                StringBuilder binaryChar = new StringBuilder(bits);
                while (binaryChar.length() < 10) {
                    binaryChar.insert(0, "0");
                }
                builder.append(binaryChar);
                i += 3;
            }
        }
        return builder.toString();
    }

    public static String bitsToNumericString(String value) {
        var builder = new StringBuilder();
        var i = 0;
        while (i < value.length()) {
            if (value.length() - i == 7) {
                var bits7 = value.substring(i, i + 7);
                var number = Integer.parseUnsignedInt(bits7, 2);
                builder.append(number);
                i += 7;
            } else if (value.length() - i == 4) {
                var bits4 = value.substring(i, i+4);
                var number = Integer.parseUnsignedInt(bits4, 2);
                builder.append(number);
                i += 4;
            } else {
                var bits10 = value.substring(i, i+10);
                var number = Integer.parseUnsignedInt(bits10, 2);
                builder.append(number);
                i += 10;
            }
        }
        return builder.toString();
    }

    public static String utf8ToIso88591(String data) {
        byte[] iso88591Bytes = data.getBytes(StandardCharsets.ISO_8859_1);
        return new String(iso88591Bytes, StandardCharsets.ISO_8859_1);
    }
    public static byte[] utf8ToIso88591Bytes(String data) {
        return data.getBytes(StandardCharsets.ISO_8859_1);
    }
    public static byte[] utf8ToKanjiBytes(String data) {
        try {
            return data.getBytes("Shift_JIS");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    public static String kanjiBytesToUtf8(byte[] data) {
        try {
            return new String(data,"Shift_JIS");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public static String iso88591BytesToUtf8(byte[] data) {
        String isoString = new String(data, StandardCharsets.ISO_8859_1);
        byte[] utf8Bytes = isoString.getBytes(StandardCharsets.UTF_8);
        return new String(utf8Bytes, StandardCharsets.UTF_8);
    }

    // https://www.thonky.com/qr-code-tutorial/byte-mode-encoding
    public static String byteStringToBits(String data) {
        return bytesToBits(utf8ToIso88591Bytes(data));
    }
    public static String byteToBits(byte singleByte) {
        return intToBits(singleByte & 0xFF, 8);
    }
    public static String bytesToBits(byte[] bytes) {
        var builder = new StringBuilder();
        for (byte isoByte : bytes) {
            builder.append(intToBits(isoByte, 8));
        }
        return builder.toString();
    }
    public static String bitsToByteString(String data) {
        var builder = new StringBuilder();
        var isoBytes = new byte[data.length() / 8];
        var bIndex = 0;
        for (int i = 0; i < data.length(); ) {
            var bits = data.substring(i, i+8);
            var b = Byte.parseByte(bits, 2);
            isoBytes[bIndex] = b;
            i += 8;
            bIndex++;
        }
        return iso88591BytesToUtf8(isoBytes);
    }

    private static long KANJI_1_START = Long.parseLong("8140", 16);
    private static long KANJI_1_END = Long.parseLong("9FFC", 16);
    private static long KANJI_1_SUBTRACT = Long.parseLong("8140", 16);
    private static long KANJI_MULT = Long.parseLong("C0", 16);


    private static long KANJI_2_START = Long.parseLong("E040", 16);
    private static long KANJI_2_END = Long.parseLong("EBBF", 16);
    private static long KANJI_2_SUBTRACT = Long.parseLong("C140", 16);


    public static String kanjiStringToBits(String data) {
        var kanjiBytes = utf8ToKanjiBytes(data);
        var builder = new StringBuilder();
        for (int i = 0; i < kanjiBytes.length; i += 2) {
            if (i + 1 < kanjiBytes.length) {
                // byte in java is -128 -> 127
                long value = ((long)kanjiBytes[i]  & 0xFFL) * 256L + ((long)kanjiBytes[i+1] & 0xFFL);
                long encodedValue = 0;
                if (KANJI_1_START <= value && value <= KANJI_1_END) {
                    value = value - KANJI_1_SUBTRACT;
                    encodedValue = (value / 256) * KANJI_MULT + (value % 256);
                } else if (KANJI_2_START <= value && value <= KANJI_2_END){
                    value = value - KANJI_2_SUBTRACT;
                    encodedValue = (value / 256) * KANJI_MULT + (value % 256);
                } else {
                    throw new IllegalArgumentException("Not a KANJI character");
                }
                var bits = uintToBits(encodedValue, 13);
                builder.append(bits);
            } else {
                throw new IllegalArgumentException("kanji bytes is not aligned by 2" );
            }
        }
        return builder.toString();
    }

    public static String bitsToKanjiString(String data) {
        var builder = new StringBuilder();
        var kanjiBytes = new byte[data.length() / 13 * 2];
        var bIndex = 0;
        for (int i = 0; i < data.length(); ) {
            var bits = data.substring(i, i+13);
            var encodedValue = Long.parseLong(bits, 2);
            var highValue = encodedValue / KANJI_MULT;
            var lowValue = encodedValue % KANJI_MULT;
            var value = highValue * 256L + lowValue;
            if (KANJI_1_START <= (value+KANJI_1_SUBTRACT) && (value+KANJI_1_SUBTRACT) <= KANJI_1_END) {
                value = value + KANJI_1_SUBTRACT;
            } else {
                value = value + KANJI_2_SUBTRACT;
            }
            var high = (byte)(value / 256L);
            var low = (byte)(value % 256L);
            kanjiBytes[bIndex] = high;
            kanjiBytes[bIndex+1] = low;
            i += 13;
            bIndex += 2;
        }
        return kanjiBytesToUtf8(kanjiBytes);
    }

}