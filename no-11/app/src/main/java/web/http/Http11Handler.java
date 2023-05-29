package web.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class Http11Handler implements IHttpHandler {

    public Http11Handler() {

    }

    public Optional<String> validAction(String action) {
        try {
            return Optional.of(Actions.valueOf(action.toUpperCase()).toString());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public byte[] request(ByteBuffer byteBuffer) {
        try {
            var req = new InternalRequest(this, byteBuffer);
            var webReq = req.getBody();
            return webReq.getBody();

        } catch (IOException e) {
            return e.getMessage().getBytes();
        }
    }

    public enum Actions {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE,
        HEAD,
        OPTIONS,
        TRACE,
        CONNECT
    }
}
