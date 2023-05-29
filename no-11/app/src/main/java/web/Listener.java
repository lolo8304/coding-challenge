package web;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import web.http.Http11Handler;

public class Listener {
    static final Logger _logger = Logger.getLogger(Listener.class.getName());
    private Http11Handler handler;

    public Listener(int port, Http11Handler handler) throws InterruptedException {
        this.handler = handler;
        start(port);
    }

    @SuppressWarnings("java:S2189")
    protected void start(int port) throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ListenerChannelInitializer(this))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            // This will block until the server is shut down.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    public static class ListenerCommandLineHandler extends ChannelInboundHandlerAdapter {

        private Listener server;

        public ListenerCommandLineHandler(Listener server) {
            this.server = server;

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            var nettyBufferIn = (ByteBuf) msg;
            byte[] bytes = new byte[nettyBufferIn.readableBytes()];
            nettyBufferIn.readBytes(bytes);
            // important to position correctly
            var nioBufferIn = ByteBuffer.wrap(bytes);

            if (_logger.isLoggable(Level.INFO)) {
                var requestString = new String(bytes);
                byte[] requestBytes = requestString.getBytes();
                _logger.info("Request: " + requestBytes.length + " - " +
                        requestString);
            }

            byte[] bytesResponse = server.handler.request(nioBufferIn);
            ByteBuf out = Unpooled.copiedBuffer(bytesResponse);
            ctx.write(out);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

    }

    public static class ListenerChannelInitializer extends ChannelInitializer<SocketChannel> {

        private Listener server;

        public ListenerChannelInitializer(Listener server) {
            this.server = server;
        }

        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new ListenerCommandLineHandler(this.server));
        }
    }
}
