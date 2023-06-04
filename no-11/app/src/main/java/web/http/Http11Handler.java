package web.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import web.http.servlets.NoServlet;
import web.http.servlets.Routes;
import web.http.servlets.StaticFileServlet;
import web.http.servlets.examples.HelloRest;
import web.http.servlets.examples.HelloWorldRest;

public class Http11Handler implements IHttpHandler {

    private final Routes routes;

    public Http11Handler(String webRoot) {
        this.routes = new Routes();
        this.routes.add("GET", "/", new StaticFileServlet(webRoot));
        this.routes.add("GET", "/hello", new HelloRest());
        this.routes.add("GET", "/hello/world", new HelloWorldRest());
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

    @Override
    public HttpWebResponse request(HttpWebRequest req) {
        var route = this.routes.match(req.method(), req.uri().toString());
        if (route.isPresent()) {
            return route.get().servlet.request(req);
        } else {
            return new NoServlet().request(req);
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
