package dns;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DnsMessage {
    private static final Random random = new SecureRandom();
    private int id;

    private int flags;

    private List<DnsQuestion> questions = new ArrayList<>();

    private List<DnsResourceRecord> answers = new ArrayList<>();

    private List<DnsResourceRecord> authorities = new ArrayList<>();

    private List<DnsResourceRecord> additionalRecords = new ArrayList<>();

    public static int generate16BitIdentifier() {
        return random.nextInt(1 << 16);
    }

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

    public DnsMessage() {
        this(generate16BitIdentifier(), Flags.QR_QUERY);
    }

    public DnsMessage(int id, int flags) {
        this.id = id;
        this.flags = flags;
    }

    public DnsMessage(DnsMessage request) {
        this();
        this.id = request.getId();
    }

    public int getId() {
        return id;
    }

    public int getFlags() {
        return flags;
    }

    public int getQuestionCount() {
        return this.questions.size();
    }

    public int getAnswerCount() {
        return this.answers.size();
    }

    public int getAuthorityCount() {
        return this.authorities.size();
    }

    public int getAdditionalCount() {
        return this.additionalRecords.size();
    }

    public void setQuestion(DnsQuestion question) {
        this.questions.clear();
        this.questions.add(question);
    }

    public List<DnsQuestion> getQuestions() {
        return questions;
    }

    public List<DnsResourceRecord> getAnswers() {
        return answers;
    }

    public List<DnsResourceRecord> getAuthorities() {
        return authorities;
    }

    public List<DnsResourceRecord> getAdditionalRecords() {
        return additionalRecords;
    }

    public StringBuilder buildHeader(StringBuilder builder) {
        builder.append(intToHexWithLeadingZeros(this.id, 2));
        builder.append(intToHexWithLeadingZeros(this.flags, 2));
        builder.append(intToHexWithLeadingZeros(this.getQuestionCount(), 2));
        builder.append(intToHexWithLeadingZeros(this.getAnswerCount(), 2));
        builder.append(intToHexWithLeadingZeros(this.getAuthorityCount(), 2));
        builder.append(intToHexWithLeadingZeros(this.getAdditionalCount(), 2));
        return builder;
    }

    public StringBuilder build(StringBuilder builder) {
        this.buildHeader(builder);
        this.questions.forEach((x) -> {
            x.buildHeader(builder);
        });
        return builder;
    }

    public static class Flags {

        public static String fill(int n) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < n; i++) {
                stringBuilder.append('_');
            }
            return stringBuilder.toString();
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
        public static final int RECURSION_AVAIL = /* */ binaryToInt("00000000_0000000", "1");
        public static final int Z_RESERVED = /*      */ binaryToInt("000000000___0000", "000");
        public static final int RC_NO_ERROR = /*     */ binaryToInt("000000000000____", "0000");
        public static final int RC_FORMAT_ERROR = /* */ binaryToInt("000000000000____", "0001");
        public static final int RC_SERVER_ERROR = /* */ binaryToInt("000000000000____", "0010");
        public static final int RC_NAME_ERROR = /*   */ binaryToInt("000000000000____", "0011");
        public static final int RC_NOT_IMPL = /*     */ binaryToInt("000000000000____", "0100");
        public static final int RC_REFUSED = /*      */ binaryToInt("000000000000____", "0101");

    }

    public static class HeaderFlags {
        public static final int QCLASS_INTERNET = 1;
        public static final int QCLASS_CSNET = 2;
        public static final int QCLASS_CHAOS = 3;
        public static final int QCLASS_HESIOD = 4;
        public static final int QCLASS_ANY = 255;

        public static final int QTYPE_A = 1;
        public static final int QTYPE_NS = 2;
        public static final int QTYPE_CNAME = 5;
    }
}
