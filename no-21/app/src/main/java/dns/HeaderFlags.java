package dns;

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
    public static final int QTYPE_ALL = 255;
}

