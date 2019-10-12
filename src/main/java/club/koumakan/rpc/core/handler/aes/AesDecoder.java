package club.koumakan.rpc.core.handler.aes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import javax.crypto.Cipher;
import java.util.List;


@ChannelHandler.Sharable
public class AesDecoder extends MessageToMessageDecoder<ByteBuf> {

    private Cipher cipher;

    public AesDecoder(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] ciphertext = new byte[in.readableBytes()];
        in.readBytes(ciphertext);
        byte[] plaintext = cipher.doFinal(ciphertext);
        ByteBuf byteBuf = ctx.alloc().buffer().writeBytes(plaintext);
        out.add(byteBuf);
    }
}
