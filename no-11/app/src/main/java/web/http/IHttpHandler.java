package web.http;

import java.nio.ByteBuffer;
import java.util.Optional;

import io.netty.channel.ChannelHandlerContext;

public interface IHttpHandler {
    public Optional<String> validAction(String action);

    public void request(ChannelHandlerContext ctx, ByteBuffer byteBuffer);

    public HttpWebResponse request(HttpWebRequest req);
}
