package dns;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Random;

public class OctetHelper {
    private static final Random random = new SecureRandom();

    public static int generate16BitIdentifier() {
        return random.nextInt(1 << 16);
    }

    @SuppressWarnings("unused")
    public static String formatWithLeadingZeros(int number, int width) {
        return String.format("%0" + width + "d", number);
    }

    public static String intToHexWithLeadingZeros(int number, int width) {
        return String.format("%0" + width * 2 + "X", number);
    }

    public static String stringToHex(String input) {
        StringBuilder hexStringBuilder = new StringBuilder();

        byte[] bytes = input.getBytes();
        for (byte b : bytes) {
            // Convert byte to hexadecimal
            String hex = String.format("%02X", b).toUpperCase();
            hexStringBuilder.append(hex);
        }

        return hexStringBuilder.toString();
    }

    public static String hexToString(String hex) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < hex.length(); i += 2) {
            String pair = hex.substring(i, i + 2);
            int charCode = Integer.parseInt(pair, 16);
            stringBuilder.append((char) charCode);
        }

        return stringBuilder.toString();
    }

    public static int hexToInteger(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public static byte[] hexStringToByteArray(String hex) {
        int length = hex.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] data, int length) {
        StringBuilder hex = new StringBuilder(length * 2);
        for (int i = 0; i < length; i++) {
            hex.append(String.format("%02X", data[i]));
        }
        return hex.toString();
    }

    @SuppressWarnings("unused")
    public static boolean isValidIPAddress(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
