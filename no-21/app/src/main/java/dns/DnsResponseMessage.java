package dns;

public class DnsResponseMessage extends DnsMessage {
    private final String responseHexString;

    public DnsResponseMessage(DnsMessage request, String responseHexString) {
        super(request);
        this.responseHexString = responseHexString;
    }

    @Override
    public OctetWriter write(OctetWriter writer) {
        writer.append(this.responseHexString);
        return writer;
    }
}
