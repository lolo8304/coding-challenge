package web.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpWebRequest extends java.net.http.HttpRequest {

    private static final Optional<Duration> DEFAULT_TIMEOUT = Optional.of(Duration.ofSeconds(30));

    private final InternalRequest internalRequest;
    private URI uri;
    private Version protocol;
    private String action;
    private Map<String, List<String>> headers;
    private byte[] body;

    public HttpWebRequest(InternalRequest internalRequest) {
        this.internalRequest = internalRequest;
        this.headers = new HashMap<>();
        this.protocol = Version.HTTP_1_1;
        this.action = "GET";
    }

    @Override
    public Optional<BodyPublisher> bodyPublisher() {
        throw new UnsupportedOperationException("Unimplemented method 'bodyPublisher'");
    }

    @Override
    public Optional<Duration> timeout() {
        return DEFAULT_TIMEOUT;
    }

    @Override
    public boolean expectContinue() {
        throw new UnsupportedOperationException("Unimplemented method 'expectContinue'");
    }

    @Override
    public String method() {
        return this.action;
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public Optional<Version> version() {
        return Optional.of(protocol);
    }

    @Override
    public HttpHeaders headers() {
        return HttpHeaders.of(this.headers, (key, value) -> {
            return true;
        });
    }

    public void addHeader(String key, String value) {
        var list = this.headers.get(key);
        if (list != null) {
            list.add(value);
        } else {
            var newList = new ArrayList<String>();
            newList.add(value);
            this.headers.put(key, newList);
        }
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setProtocol(Version protocol) {
        this.protocol = protocol;
    }

    public void setUri(String uriString) throws URISyntaxException {
        this.uri = new URI(uriString);
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setBody(byte[] bytes) {
        this.body = bytes;
    }

    public String getText() {
        return new String(this.body);
    }

    public byte[] getBody() throws IOException {
        if (!this.internalRequest.hasBody()) {
            this.internalRequest.getBody();
        }
        return this.body;
    }

}
