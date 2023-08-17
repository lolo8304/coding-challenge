package dns;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@SuppressWarnings("unused")
public class OctetReader {

    public static final int POINTER_MASK16 = 0xC000;
    public static final int POINTER_MASK8 = 0xC0;
    public static final int OFFSET_MASK16 = 0xFFFF - POINTER_MASK16;
    private final StringReader reader;
    private final String octetString;
    private final OctetReader parentReader;


    public OctetReader(String octetString) {
        this.octetString = octetString;
        this.reader = new StringReader(octetString);
        this.parentReader = null;
    }
    public OctetReader(byte[] octetBytes) {
        this.octetString = new String(octetBytes, StandardCharsets.UTF_8);
        this.reader = new StringReader(octetString);
        this.parentReader = null;
    }
    public OctetReader(byte[] octetBytes, OctetReader parentReader) {
        this.octetString = new String(octetBytes, StandardCharsets.UTF_8);
        this.reader = new StringReader(octetString);
        this.parentReader = parentReader;
    }

    public OctetReader(String octetString, int offset, OctetReader parentReader) throws IOException {
        this.octetString = octetString;
        this.reader = new StringReader(octetString);
        this.parentReader = parentReader;
        if (offset > 0) {
            var readChars = this.reader.read(new char[offset*2]);
            if (readChars != offset * 2) {
                throw new IOException(String.format("Read only %d instead of %d chars. end of stream", readChars, offset * 2));
            }
            this.reader.mark(0); // reader does not have real mark, just >= 0
        }
    }

    public Optional<String> readHex() throws IOException {
        return this.readHex(1);
    }

    public Optional<Integer> readByte() throws IOException {
        return this.readHex().map(OctetHelper::hexToInteger);
    }
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public Optional<String> readIpAddress() throws IOException {
        var ipPart1 = this.readByte().get();
        var ipPart2 = this.readByte().get();
        var ipPart3 = this.readByte().get();
        var ipPart4 = this.readByte().get();
        return Optional.of(String.format("%d.%d.%d.%d", ipPart1, ipPart2, ipPart3, ipPart4));
    }

    public Optional<byte[]> readBytes(int count) throws IOException {
        var str = this.readHex(count);
        return str.map(s -> s.getBytes(StandardCharsets.UTF_8));
    }


    public Optional<Integer> readInt16() throws IOException {
        return this.readHex(2).map(OctetHelper::hexToInteger);
    }

    public Optional<Integer> readInt32() throws IOException {
        return this.readHex(4).map(OctetHelper::hexToInteger);
    }

    public Optional<String> readString(int count) throws IOException {
        return this.readHex(count).map(OctetHelper::hexToString);
    }

    public Optional<String> readHex(int count) throws IOException {
        var buffer = new char[count*2];
        var charsRead = reader.read(buffer);
        if (charsRead == count*2) {
            return Optional.of(new String(buffer));
        }
        return Optional.empty();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public Optional<String> readName() throws IOException {
        int len = this.readInt16().get();
        var prefix = len & POINTER_MASK16; // first 2 bit are indicators for cached entry
        var offset = len & OFFSET_MASK16; // all the other bits are index
        return (prefix > 0) ? this.readQName(offset) : this.readHex(len);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public Optional<String> readQName() throws IOException {
        var builder = new StringBuilder();
        var hex = this.readHex(1);
        while (hex.isPresent()) {
            int count = OctetHelper.hexToInteger(hex.get());
            if (count == 0) { // final entry
                return Optional.of(builder.toString());
            }
            Optional<String> label;
            var hasOffsetMask = (count & POINTER_MASK8) > 0;
            if (hasOffsetMask) { // cache entry
                hex = this.readHex(1);
                var ptrIndex = OctetHelper.hexToInteger(hex.get());
                var offsetInside = ((count << 8) + ptrIndex) & OFFSET_MASK16;
                var topReader = this.topParentReader();
                if (topReader != null) {
                    var qName = topReader.readQName(offsetInside);
                    if (qName.isPresent()) {
                        label = qName;
                    } else {
                        throw new IOException(String.format("QName could not be found for offset %d", offsetInside));
                    }
                } else {
                    throw new IOException(String.format("No parent reader found for QName with offset %d not found", offsetInside));
                }
            } else {
                label = this.readString(count);
            }
            if (label.isPresent()) {
                if (!builder.isEmpty()) {
                    builder.append('.');
                }
                builder.append(label.get());
                if (hasOffsetMask) {
                    return Optional.of(builder.toString());
                }
                hex = this.readHex(1);
            } else {
                throw new IOException("error in parsing octet string from stream");
            }
        }
        throw new IOException("error in parsing octet string from stream");
    }
    private OctetReader topParentReader() {
        if (this.parentReader == null) {
            return this;
        } else {
            return this.parentReader.topParentReader();
        }
    }

    public Optional<String> readQName(int offset) throws IOException {
        var offsetReader = new OctetReader(this.octetString, offset, this);
        return offsetReader.readQName();
    }

}
