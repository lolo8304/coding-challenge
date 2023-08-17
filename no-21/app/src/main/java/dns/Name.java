package dns;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

public class Name {

    public final String domainName;
    public final String octetString;

    public static Name fromOctetName(String octetName) throws IOException {
        return new Name(octetName, true);
    }

    public static Name fromDomainName(String domainName) throws IOException {
        return new Name(domainName, false);
    }

    public Name(String domainName) throws IOException {
        this(domainName, false);
    }
    public Name(String name, boolean isOctetString) throws IOException {
        this.octetString = isOctetString ? name : toOctetString(name);
        this.domainName = isOctetString ? fromOctetString(name): name;
    }

    public static String fromOctetStream(OctetReader reader) throws IOException {
        var builder = new StringBuilder();
        var hex = reader.readHex(1);
        while (hex.isPresent()) {
            int count = DnsMessage.hexToInteger(hex.get());
            if (count == 0) {
                return builder.toString();
            }
            var label = reader.readString(count);
            if (label.isPresent()) {
                if (!builder.isEmpty()) {
                    builder.append('.');
                }
                builder.append(label.get());
                hex = reader.readHex(1);
            } else {
                throw new IOException("error in parsing octet string from stream");
            }
        }
        throw new IOException("error in parsing octet string from stream");
    }

    public static String fromOctetString(String octetString) throws IOException {
        return fromOctetStream(new OctetReader(octetString));
    }

    public static String fromOctetBytes(byte[] octetBytes) throws IOException {
        return fromOctetStream(new OctetReader(octetBytes));
    }

    public static String toOctetString(String name) {
        var list = Arrays.stream(name.split("\\.")).map(
            (x) -> DnsMessage.intToHexWithLeadingZeros(x.length(), 1)
                    + DnsMessage.stringToHex(x)).toList();
        return String.join("", list) + DnsMessage.intToHexWithLeadingZeros(0, 1);
    }
}
