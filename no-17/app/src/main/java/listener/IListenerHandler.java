package listener;

import java.io.IOException;
import java.nio.ByteBuffer;

import io.netty.channel.ChannelHandlerContext;

public interface IListenerHandler {
    public void request(ChannelHandlerContext ctx, ByteBuffer nioBufferIn) throws IOException;

}
