package dns;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class DnsMessage {
    private static final Random random = new SecureRandom();
    private int id;

    private final int flags;

    private final List<DnsQuestion> questions = new ArrayList<>();

    private final List<DnsResourceRecord> answers = new ArrayList<>();

    private final List<DnsResourceRecord> authorities = new ArrayList<>();

    private final List<DnsResourceRecord> additionalRecords = new ArrayList<>();

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

    public DnsMessage(OctetReader reader) throws IOException {
        this.id = reader.readInt16().get();
        this.flags = reader.readInt16().get();

        var questionsCount = reader.readInt16().get();
        var answersCount = reader.readInt16().get();
        var authoritiesCount = reader.readInt16().get();
        var additionalsCount = reader.readInt16().get();

        for (int i = 0; i < questionsCount; i++) {
            this.questions.add(new DnsQuestion(reader));
        }
        for (int i = 0; i < answersCount; i++) {
            this.answers.add(new DnsResourceRecord(reader));
        }
        for (int i = 0; i < authoritiesCount; i++) {
            this.authorities.add(new DnsResourceRecord(reader));
        }
        for (int i = 0; i < additionalsCount; i++) {
            this.additionalRecords.add(new DnsResourceRecord(reader));
        }
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

    public DnsMessage addQuestion(DnsQuestion question) {
        this.questions.clear();
        this.questions.add(question);
        return this;
    }

    public List<DnsQuestion> getQuestions() {
        return questions;
    }

    public List<DnsResourceRecord> getAnswers() {
        return answers;
    }

    public List<String> getIpAddresses() {
        var list = new ArrayList<>(this.answers);
        list.sort( (x,y) -> {
            return Long.compare(x.getIpAddressLong(), y.getIpAddressLong());
        });
        return list.stream().map(DnsResourceRecord::getIpAddress).filter(Optional::isPresent).map(Optional::get).toList();
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

    public StringBuilder build(StringBuilder builder) throws IOException {
        this.buildHeader(builder);
        for (int i = 0; i < this.questions.size(); i++) {
            this.questions.get(i).buildHeader(builder);
        }
        return builder;
    }

    public String send(String dnsServer, int port) throws IOException {
        var hexMessageToSend = this.build(new StringBuilder()).toString();
        return transfer(dnsServer, port, hexMessageToSend).build(new StringBuilder()).toString();
    }

    private DnsMessage transfer(String dnsServer, int port, String hexMessageToSend) throws IOException {
        var server = new DnsServer(dnsServer, port);
        var hexMessageReceived = server.sendAndReceive(hexMessageToSend);
        return new DnsResponseMessage(this, hexMessageReceived);
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
        public static final int QTYPE_SOA = 6;
        public static final int QTYPE_WKS = 11;
        public static final int QTYPE_PTR = 12;
        public static final int QTYPE_HINFO = 13;
        public static final int QTYPE_MX = 15;
        public static final int QTYPE_TXT = 16;
        public static final int QTYPE_All = 255;
    }
}
