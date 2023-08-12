package dns;

import java.util.Arrays;

import dns.DnsMessage.HeaderFlags;

public class DnsQuestion {
    private String name;
    private int type;
    private int clazz;

    public DnsQuestion() {
    }

    public DnsQuestion(String name) {
        this(name, HeaderFlags.QCLASS_INTERNET, HeaderFlags.QTYPE_A);
    }

    public DnsQuestion(String name, int type, int clazz) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
    }

    public String getName() {
        return this.name;
    }

    public int getType() {
        return this.type;
    }

    public int getClazz() {
        return this.clazz;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setClazz(int clazz) {
        this.clazz = clazz;
    }

    public StringBuilder buildHeader(StringBuilder builder) {
        builder.append(DnsMessage.stringToHex(this.encodedName()));
        builder.append(DnsMessage.intToHexWithLeadingZeros(this.getType(), 2));
        builder.append(DnsMessage.intToHexWithLeadingZeros(this.getClazz(), 2));
        return builder;
    }

    public String encodedName() {
        var list = Arrays.asList(this.name.split("\\.")).stream().map((x) -> "" + x.length() + x).toList();
        return String.join("", list) + "0";
    }
}