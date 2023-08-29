package dns;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public static final int OPCODE_MASK = /*     */ binaryToInt("0____00000000000", "1111");

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
    public static final int RC_MASK = /*         */ binaryToInt("000000000000____", "1111");

    public static List<String> flags(int flags) {
        var list = new ArrayList<String>();
        flag(flags, QR_RESPONSE, "RESPONSE", "REQUEST").ifPresent(list::add);
        flag(flags, OPCODE_MASK, OPCODE_QUERY, "Q").ifPresent(list::add);
        flag(flags, OPCODE_MASK, OPCODE_IQUERY, "IQ").ifPresent(list::add);
        flag(flags, OPCODE_MASK, OPCODE_STATUS, "STATUS").ifPresent(list::add);
        flag(flags, AUTHORITATIVE_ANSWERS, "NS").ifPresent(list::add);
        flag(flags, TRUNCATION, "TRUNC").ifPresent(list::add);
        flag(flags, RECURSION_DESIRED, null,"NORECURSE").ifPresent(list::add);
        flag(flags, RECURSION_AVAIL, "REC_AVAIL").ifPresent(list::add);
        flag(flags, RC_MASK, RC_FORMAT_ERROR, "ERR_FORMAT").ifPresent(list::add);
        flag(flags, RC_MASK, RC_NAME_ERROR, "ERR_NAME").ifPresent(list::add);
        flag(flags, RC_MASK, RC_REFUSED, "ERR_REFUSED").ifPresent(list::add);
        flag(flags, RC_MASK, RC_SERVER_ERROR, "ERR_SERVER").ifPresent(list::add);
        flag(flags, RC_MASK, RC_SERVER_ERROR, "ERR_SERVER").ifPresent(list::add);
        flag(flags, RC_MASK, RC_NOT_IMPL, "ERR_NOT_IMPL").ifPresent(list::add);
        return list;
    }
    public static Optional<String> flag(int flags, int mask, int flag, String flag1, String flag0) {
        if ((flags & mask) == flag) {
            return flag1 == null ? Optional.empty() : Optional.of(flag1);
        } else {
            return flag0 == null ? Optional.empty() : Optional.of(flag0);
        }
    }
    public static Optional<String> flag(int flags, int mask, String flag1, String flag0) {
        return flag(flags, mask, mask, flag1, flag0);
    }
    public static Optional<String> flag(int flags, int mask, int flag, String flag1) {
        return flag(flags, mask, flag, flag1, null);
    }
    public static Optional<String> flag(int flags, int mask, String flag1) {
        return flag(flags, mask, mask, flag1, null);
    }
}

