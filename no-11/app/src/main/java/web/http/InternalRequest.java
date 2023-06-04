package web.http;

import java.io.IOException;
import java.nio.ByteBuffer;

public class InternalRequest {
    private HttpScanner scanner;

    private final IHttpHandler handler;
    private final HttpWebRequest request;

    private boolean parsedHeaders = false;
    private boolean parsedBody = false;

    public InternalRequest(IHttpHandler handler, ByteBuffer byteBuffer) {
        this.handler = handler;
        this.request = new HttpWebRequest(this);
        this.scanner = new HttpScanner(this.handler, byteBuffer);
    }

    private void parseUntilBody() throws IOException {
        var tokenValue = scanner.next();
        while (tokenValue.isPresent() && !tokenValue.get().isBody()) {
            tokenValue.get().applyToRequest(request);
            tokenValue = scanner.next();
        }
        this.parsedHeaders = true;
        if (tokenValue.isEmpty() || tokenValue.get().isEof()) {
            this.parsedBody = true;
            request.setBody("".getBytes());
        }
    }

    private void parseBody() throws IOException {
        var contentLength = this.request.headers().firstValue("Content-Length");
        if (contentLength.isPresent()) {
            var length = Integer.parseInt(contentLength.get());
            var tokenValue = scanner.nextBody(length);
            if (tokenValue.isPresent()) {
                tokenValue.get().applyToRequest(request);
            } else {
                throw new IOException("Content-Length set to " + length + " but no content available");
            }
        } else {
            this.request.setBody("".getBytes());
        }
        this.parsedBody = true;
    }

    public HttpWebRequest getRequest() throws IOException {
        if (!parsedHeaders) {
            this.parseUntilBody();
        }
        return this.request;
    }

    public HttpWebRequest getBodyAndRequest() throws IOException {
        if (!this.parsedBody) {
            this.getRequest();
            this.parseBody();
        }
        return this.request;
    }

    public boolean hasBody() {
        return this.parsedBody;
    }

}
