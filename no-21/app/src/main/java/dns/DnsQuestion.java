package dns;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import dns.DnsMessage.HeaderFlags;

public class DnsQuestion {
    private String name;
    private int type;
    private int clazz;

    public DnsQuestion() {
    }

    public DnsQuestion(String name) {
        this(name, HeaderFlags.QTYPE_All);
    }

    public DnsQuestion(String name, int clazz, int type) {
        this.name = name;
        this.clazz = clazz;
        this.type = type;
    }

    public DnsQuestion(String name, int type) {
        this(name, HeaderFlags.QCLASS_INTERNET, type);
    }

    public DnsQuestion(OctetReader reader) throws IOException {
        this.name = Name.fromOctetStream(reader);
        this.type = reader.readInt16().get();
        this.clazz = reader.readInt16().get();
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

    public StringBuilder buildHeader(StringBuilder builder) throws IOException {
        builder.append(this.getOctetString());
        builder.append(DnsMessage.intToHexWithLeadingZeros(this.getType(), 2));
        builder.append(DnsMessage.intToHexWithLeadingZeros(this.getClazz(), 2));
        return builder;
    }

    public String getOctetString() throws IOException {
        return new Name(this.name).octetString;
    }

}