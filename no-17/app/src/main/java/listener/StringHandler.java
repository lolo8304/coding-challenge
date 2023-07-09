package listener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public abstract class StringHandler implements IListenerHandler {

    public static BufferedReader convertByteBufferToBufferedReader(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }

    @Override
    public void request(ChannelHandlerContext ctx, ByteBuffer nioBufferIn) throws IOException {
        var bufferedInputStream = convertByteBufferToBufferedReader(nioBufferIn);
        var response = this.request(bufferedInputStream);
        ByteBuf out = this.stringToByteBuf(response);
        ctx.write(out);
    }

    private ByteBuf stringToByteBuf(String response) {
        return Unpooled.copiedBuffer(response, StandardCharsets.UTF_8);
    }

    public abstract String request(BufferedReader bufferedReader) throws IOException;

}
