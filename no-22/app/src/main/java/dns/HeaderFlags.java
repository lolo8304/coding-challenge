package dns;

import java.util.Arrays;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class HeaderFlags {

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
    public static final int QTYPE_AAAA = 28;
    public static final int QTYPE_ALL = 255;


    public enum Clazz {
        INTERNET(1),
        CSNET(2),
        CHAOS(3),
        HESIOD(4),
        ANY(255);

        private final int value;

        private Clazz(int value) {
            this.value = value;
        }
        public int value() {return value;}
    }
    public enum Type {
        A(1),
        NS(2),
        CNAME(5),
        SOA(6),
        WKS(11),
        PTR(12),
        HINFO(13),
        MX(15),
        TXT(16),
        AAAA(28),
        ALL(255);
        private final int value;

        private Type(int value) {
            this.value = value;
        }
        public static String stringValueOf(int value) {
            for (Type t : Type.values()) {
                if (t.value == value) {
                    return t.toString();
                }
            }
            return String.format("Unknown<%s>", value);
        }
        public static Type valueOf(int value) {
            for (Type t : Type.values()) {
                if (t.value == value) {
                    return t;
                }
            }
            throw new IllegalArgumentException("No matching enum value for " + value);
        }
        public static boolean isValid(int value) {
            return Arrays.stream(Type.values()).anyMatch(t -> t.value == value);
        }

        public int value() {return value;}

    }
}

