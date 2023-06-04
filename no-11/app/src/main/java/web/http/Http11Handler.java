package web.http;

import java.io.IOException;
import java.net.http.HttpClient.Version;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class Http11Handler implements IHttpHandler {

    public static final Http11Handler INSTANCE = new Http11Handler();

    private Http11Handler() {

    }

    public Optional<String> validAction(String action) {
        try {
            return Optional.of(Actions.valueOf(action.toUpperCase()).toString());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static boolean shouldCloseConnection(HttpWebRequest webRequest) {
        // Check if the "Connection" header is set to "close"
        var connectionHeader = webRequest.headers().firstValue("Connection");
        return connectionHeader.isPresent() && connectionHeader.get().equalsIgnoreCase("close");
    }

    @Override
    public void request(ChannelHandlerContext ctx, ByteBuffer byteBuffer) {
        try {
            var req = new InternalRequest(this, byteBuffer);
            var webReq = req.getBodyAndRequest();
            var webResponse = this.request(webReq);
            var strBuilder = new StringBuilder(256);
            var msgUtil = HttpMessageUtil.appendFullResponse(strBuilder, webResponse);
            ByteBuf out = Unpooled.copiedBuffer(msgUtil.toString(), StandardCharsets.UTF_8);
            ctx.write(out);
            ctx.writeAndFlush(webResponse.content());
            if (shouldCloseConnection(webReq)) {
                ctx.close();
            }

        } catch (IOException e) {
            ByteBuf out = Unpooled.copiedBuffer(e.getMessage(), StandardCharsets.UTF_8);
            ctx.write(out);
        }
    }

    private HttpWebResponse request(HttpWebRequest req) {
        var v = req.version();
        return HttpWebResponse
                .ok()
                .mediaType(MediaType.TEXT_HTML_TYPE)
                .entity("Requested Path: " + req.uri())
                .build(v.isPresent() ? v.get() : Version.HTTP_1_1, req.uri());
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
