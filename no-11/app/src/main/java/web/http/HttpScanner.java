package web.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.http.HttpClient.Version;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HttpScanner {

    private final ByteBuffer buffer;
    private int position;
    private IHttpHandler handler;

    public static String convertNewLines(String str) {
        return str.replace("\\r", "\r").replace("\\n", "\n");
    }

    public static String convertNewLinesBack(String str) {
        return str.replace("\r", "\\r").replace("\n", "\\n");
    }

    public static HttpScanner fromString(IHttpHandler handler, String str) {
        return new HttpScanner(handler, str);
    }

    public static HttpScanner fromEscapedString(IHttpHandler handler, String str) {
        return fromString(handler, convertNewLines(str));
    }

    public HttpScanner(IHttpHandler handler, String str) {
        this.handler = handler;
        var strBuffer = ByteBuffer.wrap(str.getBytes());
        this.buffer = strBuffer;
        this.position = strBuffer.position();
    }

    public HttpScanner(IHttpHandler handler, ByteBuffer buffer) {
        this.handler = handler;
        this.buffer = buffer;
        this.position = buffer.position();
    }

    public HttpScanner(IHttpHandler handler, InputStream stream) throws IOException {
        this(handler, convertToByteBuffer(stream));
    }

    private static ByteBuffer convertToByteBuffer(InputStream stream) throws IOException {
        try (ReadableByteChannel channel = Channels.newChannel(stream)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            int bytesRead;
            while ((bytesRead = channel.read(buffer)) != -1) {
                buffer.flip();
                outputStream.write(buffer.array(), buffer.arrayOffset() + buffer.position(), bytesRead);
                buffer.clear();
            }
            byte[] bytes = outputStream.toByteArray();
            return ByteBuffer.wrap(bytes);
        }
    }

    public boolean hasNext() {
        return buffer.hasRemaining() && buffer.capacity() > position;
    }

    public Optional<TokenValue> next() throws IOException {
        var line = readLine();
        if (line.isPresent()) {
            var lineString = line.get().trim();
            if (lineString.isEmpty()) {
                return Optional.of(new TokenValue(Token.START_BODY));
            } else {
                return this.validTokenValueLine(lineString);
            }
        } else {
            return Optional.of(new TokenValue(Token.EOF));
        }
    }

    public Optional<BodyTokenValue> nextBody(int contentLength) {
        if (contentLength > 0) {
            var bytes = readBytes(contentLength);
            return Optional.of(new BodyTokenValue(bytes.get()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<TokenValue> validTokenValueLine(String line) throws IOException {
        var splitBySpace = line.trim().split(" ");
        if (splitBySpace.length > 0) {
            var possibleAction = splitBySpace[0];
            var validAction = this.handler.validAction(possibleAction);
            if (validAction.isPresent()) {
                return withValidAction(splitBySpace, possibleAction);
            } else {
                return withoutValidAction(line, splitBySpace);
            }
        }
        return Optional.empty();
    }

    private Optional<TokenValue> withoutValidAction(String line, String[] splitBySpace) throws IOException {
        // is header: key:<space>.*
        var headerKey = splitBySpace[0];
        if (headerKey.endsWith(":")) {
            headerKey = headerKey.substring(0, headerKey.length() - 1);
            var headerValue = line.substring(headerKey.length() + 2)
                    .trim();
            return Optional.of(new HeaderTokenValue(headerKey, headerValue));
        } else {
            throw new IOException("Illegal header '" + headerKey + "'");
        }
    }

    private Optional<TokenValue> withValidAction(String[] splitBySpace, String possibleAction) throws IOException {
        var uri = "/";
        var protocol = "HTTP/1.1";
        if (splitBySpace.length > 1) {
            uri = splitBySpace[1];
        }
        if (splitBySpace.length > 2) {
            protocol = splitBySpace[2];
        }
        if (splitBySpace.length > 3) {
            throw new IOException("Illegal number of parameters: <= 3 exepected go " + splitBySpace.length);
        }
        return Optional.of(new ProtocolTokenValue(possibleAction, uri, protocol));
    }

    private Optional<byte[]> readBytes(int length) {
        byte[] bytes = new byte[length];
        buffer.get(bytes, 0, length);
        position += length;
        return Optional.of(bytes);
    }

    private Optional<String> readLine() {
        int start = position;
        while (buffer.hasRemaining()) {
            byte b = buffer.get(position++);
            if (b == '\r' && buffer.hasRemaining() && buffer.get(position) == '\n') {
                int end = position - 1;
                position++;
                return Optional.of(new String(buffer.array(), start, end - start, StandardCharsets.UTF_8));
            }
        }
        position = start;
        return Optional.empty();
    }

    public static class TokenValue {
        public final Token token;

        public TokenValue(Token token) {
            this.token = token;
        }

        public boolean isBody() {
            return this.token.equals(Token.START_BODY) || this.token.equals(Token.EOF);
        }

        public boolean isEof() {
            return this.token.equals(Token.EOF);
        }

        public void applyToRequest(HttpWebRequest request) throws IOException {
            // default implement does not do anything
        }
    }

    public static class ProtocolTokenValue extends TokenValue {
        public final String action;
        public final String uri;
        public final Version version;

        public ProtocolTokenValue(String action, String uri, String protocol) {
            super(Token.PROTOCOL);
            this.action = action;
            this.uri = uri;
            this.version = parseProtocol(protocol);
        }

        private Version parseProtocol(String protocol) {
            var comparableProtocol = protocol.toUpperCase();
            switch (comparableProtocol) {
                case "HTTP/1.1":
                    return Version.HTTP_1_1;
                case "HTTP/2":
                    return Version.HTTP_2;
                default:
                    return Version.HTTP_1_1;
            }
        }

        @Override
        public void applyToRequest(HttpWebRequest request) throws IOException {
            request.setAction(action);
            request.setProtocol(version);
            try {
                request.setUri(uri);
            } catch (URISyntaxException e) {
                throw new IOException("Error while parsing uri '" + uri + "'", e);
            }
        }

    }

    public static class HeaderTokenValue extends TokenValue {
        public final String key;
        public final String value;

        public HeaderTokenValue(String key, String value) {
            super(Token.HEADER);
            this.key = key;
            this.value = value;
        }

        @Override
        public void applyToRequest(HttpWebRequest request) throws IOException {
            request.addHeader(this.key, this.value);
        }
    }

    public static class BodyTokenValue extends TokenValue {
        public final byte[] body;

        public BodyTokenValue(byte[] body) {
            super(Token.HEADER);
            this.body = body;
        }

        @Override
        public void applyToRequest(HttpWebRequest request) throws IOException {
            request.setBody(body);
        }
    }

    public enum Token {
        PROTOCOL,
        HEADER,
        START_BODY,
        EOF
    }
}