package dns;

public class DnsResponseMessage extends DnsMessage {
    private final String responseHexString;

    public DnsResponseMessage(DnsMessage request, String responseHexString) {
        super(request);
        this.responseHexString = responseHexString;
    }

    @Override
    public StringBuilder buildHeader(StringBuilder builder) {
        builder.append(this.responseHexString);
        return builder;
    }
}
