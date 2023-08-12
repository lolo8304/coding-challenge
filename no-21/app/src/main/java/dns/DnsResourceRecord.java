package dns;

public class DnsResourceRecord {
    private String name;
    private int type;
    private int rClass;
    private long ttl;
    private int dataLength;
    private byte[] rData;
    
    public DnsResourceRecord() {
    }
    public DnsResourceRecord(String name, int type, int rClass, long ttl, int dataLength, byte[] rData) {
        this.name = name;
        this.type = type;
        this.rClass = rClass;
        this.ttl = ttl;
        this.dataLength = dataLength;
        this.rData = rData;
    }
    public String getName() {
        return name;
    }
    public int getType() {
        return type;
    }
    public int getrClass() {
        return rClass;
    }
    public long getTtl() {
        return ttl;
    }
    public int getDataLength() {
        return dataLength;
    }
    public byte[] getRData() {
        return rData;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setrClass(int rClass) {
        this.rClass = rClass;
    }
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }
    public void setrData(byte[] rData) {
        this.rData = rData;
    }


}