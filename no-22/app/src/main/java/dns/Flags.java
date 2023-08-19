package dns;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class Flags {

    public static String fill(int n) {
        return "_".repeat(Math.max(0, n));
    }

    public static int binaryToInt(String binaryString) {
        return Integer.parseInt(binaryString, 2);
    }

    public static int binaryToInt(String binaryString, String toFill) {
        var bitStream0Frist = binaryString.replace(fill(toFill.length()), toFill);
        return Integer.parseInt(bitStream0Frist, 2);
    }

    public static int binaryToIntReversed(String binaryString, String toFill) {
        var bitStream0Frist = binaryString.replace(fill(toFill.length()), toFill);
        var bitStream0Last = new StringBuilder(bitStream0Frist).reverse().toString();
        return Integer.parseInt(bitStream0Last, 2);
    }

    public static final int QR_QUERY = 0;
    // based on docu
    /*
                                  1  1  1  1  1  1
    0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                      ID                       |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    QDCOUNT                    |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    ANCOUNT                    |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    NSCOUNT                    |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    ARCOUNT                    |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ */
    public static final int QR_RESPONSE = /*     */ binaryToInt("_000000000000000", "1");
    public static final int OPCODE_QUERY = /*    */ binaryToInt("0____00000000000", "0000");
    public static final int OPCODE_IQUERY = /*   */ binaryToInt("0____00000000000", "0001");
    public static final int OPCODE_STATUS = /*   */ binaryToInt("0____00000000000", "0010");
    public static final int AUTHORITATIVE_ANSWERS = binaryToInt("00000_0000000000", "1");
    public static final int TRUNCATION = /*      */ binaryToInt("000000_000000000", "1");
    public static final int RECURSION_DESIRED = /**/binaryToInt("0000000_00000000", "1");
    public static final int RECURSION_DESIRED_OFF = 0;
    public static final int RECURSION_AVAIL = /* */ binaryToInt("00000000_0000000", "1");
    public static final int Z_RESERVED = /*      */ binaryToInt("000000000___0000", "000");
    public static final int RC_NO_ERROR = /*     */ binaryToInt("000000000000____", "0000");
    public static final int RC_FORMAT_ERROR = /* */ binaryToInt("000000000000____", "0001");
    public static final int RC_SERVER_ERROR = /* */ binaryToInt("000000000000____", "0010");
    public static final int RC_NAME_ERROR = /*   */ binaryToInt("000000000000____", "0011");
    public static final int RC_NOT_IMPL = /*     */ binaryToInt("000000000000____", "0100");
    public static final int RC_REFUSED = /*      */ binaryToInt("000000000000____", "0101");

}

