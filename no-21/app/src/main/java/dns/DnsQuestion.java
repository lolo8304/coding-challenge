package dns;

import java.io.IOException;

public class DnsQuestion {
    private String name;
    private int type;
    private int clazz;

    public DnsQuestion() {
    }

    public DnsQuestion(String name) {
        this(name, dns.HeaderFlags.QTYPE_All);
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
        this.name = reader.readQName().get();
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

    public OctetWriter write(OctetWriter writer) throws IOException {
        writer.appendQName(this.name);
        writer.appendInt16(this.getType());
        writer.appendInt16(this.getClazz());
        return writer;
    }

}