package redis;

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
import redis.resp.RespPipelineInlineScanner;
import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.RespScanner;
import redis.resp.cache.RedisCache;
import redis.resp.commands.RespCommand;
import redis.resp.commands.RespCommandException;
import redis.resp.commands.RespInlineCommand;
import redis.resp.commands.library.RespCommandLibrary;
import redis.resp.types.RespError;

public class RedisServer {
    static final Logger _logger = Logger.getLogger(RedisServer.class.getName());

    public final RedisCache cache;

    public RedisServer(int port) throws InterruptedException {
        this.cache = new RedisCache();
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
                    .childHandler(new RedisChannelInitializer(this))
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

    public static class RedisCommandLineHandler extends ChannelInboundHandlerAdapter {

        private RedisServer server;

        public RedisCommandLineHandler(RedisServer server) {
            this.server = server;

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws RespCommandException {
            var nettyBufferIn = (ByteBuf) msg;
            byte[] bytes = new byte[nettyBufferIn.readableBytes()];
            nettyBufferIn.readBytes(bytes);
            // important to position correctly
            var nioBufferIn = ByteBuffer.wrap(bytes);

            if (_logger.isLoggable(Level.FINER)) {
                var requestString = new String(bytes);
                var requestEscapedString = RespScanner.convertNewLinesBack(requestString);
                byte[] requestBytes = requestString.getBytes();
                _logger.info("Request: " + requestBytes.length + " - " +
                        requestEscapedString);
            }

            var response = executeCommand(nioBufferIn);
            var bytesResponse = this.getBytesFromResponse(response);
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

        private RespResponse executeInlineCommand(ByteBuffer buffer) throws RespCommandException {
            var scanner = new RespPipelineInlineScanner(buffer);
            var commands = scanner.getCommands();
            var responses = new RespResponse[commands.size()];

            for (int i = 0; i < commands.size(); i++) {
                RespInlineCommand command = commands.get(i);
                var request = new RespRequest(this.server.cache, command.toCommand());
                responses[i] = RespCommandLibrary.INSTANCE.execute(request);
            }

            return RespResponse.join(responses);
        }

        private RespResponse executeCommand(ByteBuffer buffer) throws RespCommandException {
            buffer.mark();
            if (!RespScanner.isValidRespTypeChar(buffer.get())) {
                buffer.reset();
                return executeInlineCommand(buffer);
            }
            buffer.reset();
            try {
                var scanner = new RespScanner(buffer);
                var commands = scanner.getCommands();
                var responses = new RespResponse[commands.size()];

                for (int i = 0; i < commands.size(); i++) {
                    RespCommand command = commands.get(i);
                    var request = new RespRequest(this.server.cache, command);
                    responses[i] = RespCommandLibrary.INSTANCE.execute(request);
                }
                return RespResponse.join(responses);
            } catch (RespCommandException e) {
                return new RespResponse(new RespError(e.getMessage()));
            }
        }

        public byte[] getBytesFromResponse(RespResponse response) {
            if (_logger.isLoggable(Level.FINER)) {
                var resultString = response.toRespString();
                var escapedString = RespScanner.convertNewLinesBack(resultString);
                byte[] bytes = resultString.getBytes();
                _logger.info("Response: " + bytes.length + " - " + escapedString);
                return bytes;
            } else {
                return response.toRespString().getBytes();
            }
        }
    }

    public static class RedisChannelInitializer extends ChannelInitializer<SocketChannel> {

        private RedisServer server;

        public RedisChannelInitializer(RedisServer server) {
            this.server = server;

        }

        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new RedisCommandLineHandler(this.server));
        }
    }
}
