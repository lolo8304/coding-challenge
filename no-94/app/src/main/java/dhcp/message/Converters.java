package dhcp.message;

import java.util.Arrays;

public class Converters {

    public static byte[] convertHexToByteArray(String hex) {
        String[] hexParts = hex.split(" ");
        byte[] bytes = new byte[hexParts.length];
        for (int i = 0; i < hexParts.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexParts[i], 16);
        }
        return bytes;
    }

    public static int convertByteArrayToInt(byte[] bytes) {
        if (bytes.length != 4) {
            throw new IllegalArgumentException("Byte array must be exactly 4 bytes long");
        }
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8) |
               (bytes[3] & 0xFF);
    }

    public static int convertHexToInt(String hex) {
        return convertByteArrayToInt(convertHexToByteArray(hex));
    }

    public static byte[] convertIntToByteArray(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static byte[] convertByteToByteArray(byte b) {
        return new byte[] {
                b
        };
    }

    public static byte[] convertUIntToByteArray(int value) {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("Value must be between 0 and 65535");
        }
        return new byte[] {
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static String convertByteArraryToHexDump(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i % 16 == 0) {
                if (i > 0) {
                    sb.append("\n");
                }
                sb.append(String.format("%04x   ", i));
            }
            sb.append(String.format("%02x", bytes[i]));
            if (i % 16 != 15 && i != bytes.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static byte[] convertHexDumpIntoByteArray(String hexdump) {
            /* hex dump example
0000   01 01 06 00 40 a4 72 2c 00 00 00 00 00 00 00 00
0010   00 00 00 00 00 00 00 00 00 00 00 00 82 c1 45 d3
0020   81 94 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0030   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0040   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0050   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0060   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0070   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0080   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0090   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00a0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00b0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00c0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00d0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00e0   00 00 00 00 00 00 00 00 00 00 00 00 63 82 53 63
00f0   35 01 01 37 0b 01 79 03 06 0f 72 77 fc 5f 2c 2e
0100   39 02 05 dc 3d 07 01 82 c1 45 d3 81 94 33 04 00
0110   76 a7 00 ff 00 00 00 00 00 00 00 00 00 00 00 00
0120   00 00 00 00 00 00 00 00 00 00 00 00
         */
        var bytes = new byte[500];
        var index = 0;
        for (var line : hexdump.split("\n")) {
            var hexBytes = Arrays.stream(line.split(" ")).filter(x -> !x.equals("")).toArray(String[]::new);
            for (int i = 1; i < hexBytes.length; i++) {
                bytes[index++] = (byte) Integer.parseInt(hexBytes[i], 16);
            }
        }
        var newBytes = new byte[index];
        System.arraycopy(bytes, 0, newBytes,0,index);
        return newBytes;
    }

    public static byte[] convertAddress(byte[] address) {
        // align address to 4 bytes and add padding if necessary
        if (address == null || address.length == 0) {
            return new byte[4];
        }
        var paddedAddress = new byte[address.length + (4 - (address.length % 4)) % 4];
        System.arraycopy(address, 0, paddedAddress, 0, address.length);
        return paddedAddress;
    }
    public static byte[] convertEmptyAddress() {
        // return 4 bytes of zeroes
        return new byte[]{0, 0, 0, 0};
    }

    public static byte[] convertStringToByteArray(String s) {
        if (s == null || s.isEmpty()) {
            return new byte[0];
        }
        byte[] bytes = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            bytes[i] = (byte) s.charAt(i);
        }
        return bytes;
    }
}
