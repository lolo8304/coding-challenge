package dns;


import java.util.Arrays;

public class OctetWriter {

    private final StringBuilder builder;
    public OctetWriter(){
        this(new StringBuilder());
    }
    public OctetWriter(StringBuilder builder){
        this.builder = builder;
    }

    public OctetWriter appendQName(String name) {
        var list = Arrays.stream(name.split("\\.")).map(
                (x) -> OctetHelper.intToHexWithLeadingZeros(x.length(), 1)
                        + OctetHelper.stringToHex(x)).toList();
        var encodedMessage = String.join("", list) + OctetHelper.intToHexWithLeadingZeros(0, 1);
        this.append(encodedMessage);
        return this;
    }

    public OctetWriter append(String text) {
        this.builder.append(text);
        return this;
    }

    public OctetWriter appendInt16(int i){
        return this.append(OctetHelper.intToHexWithLeadingZeros(i, 2));
    }
    @SuppressWarnings("unused")
    public OctetWriter appendInt(int i){
        i = i & 0xFF;
        return this.append(OctetHelper.intToHexWithLeadingZeros(i, 1));
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }

}
