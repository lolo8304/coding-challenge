package dns;

public class DnsResponseMessage extends DnsMessage {
    private final String responseHexString;

    public DnsResponseMessage(DnsMessage request, String responseHexString) {
        super(request);
        this.responseHexString = responseHexString;
    }

    @SuppressWarnings("unused")
    @Override
    public OctetWriter write(OctetWriter writer) {
        writer.append(this.responseHexString);
        return writer;
    }
}
