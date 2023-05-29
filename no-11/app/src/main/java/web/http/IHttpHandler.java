package web.http;

import java.nio.ByteBuffer;
import java.util.Optional;

public interface IHttpHandler {
    public Optional<String> validAction(String action);

    public byte[] request(ByteBuffer byteBuffer);
}
