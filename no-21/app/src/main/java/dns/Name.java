package dns;

import java.util.Arrays;

public class Name {

    public final String domainName;
    public final String octetString;

    public static Name fromDomainName(String domainName){
        return new Name(domainName);
    }

    public static Name fromOctetName(String octetName) {
        return new Name(octetName, true);
    }

    public Name(String domainName) {
        this(domainName, false);
    }
    public Name(String name, boolean isOctetString) {
        this.octetString = isOctetString ? name : toOctetString(name);
        this.domainName = isOctetString ? fromOctetString(name): name;
    }

    public static String fromOctetString(String octetString) {
        var builder = new StringBuilder();
        int i = 0;
        while (i < octetString.length()) {
            int count = DnsMessage.hexToInteger(octetString.substring(i, i +2));
            if (count == 0) {
                return builder.toString();
            }
            i += 2;
            var octets = octetString.substring(i, i + count*2);
            if (!builder.isEmpty()) {
                builder.append('.');
            }
            builder.append(DnsMessage.hexToString(octets));
            i = i + count * 2;
        }
        throw new IllegalStateException(String.format("octetString '%s' is not valid format.", octetString));
    }
    public static String toOctetString(String name) {
        var list = Arrays.stream(name.split("\\.")).map(
            (x) -> DnsMessage.intToHexWithLeadingZeros(x.length(), 1)
                    + DnsMessage.stringToHex(x)).toList();
        return String.join("", list) + DnsMessage.intToHexWithLeadingZeros(0, 1);
    }
}
