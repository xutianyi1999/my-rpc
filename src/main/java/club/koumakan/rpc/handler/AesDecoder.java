package club.koumakan.rpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import javax.crypto.Cipher;
import java.util.List;

import static club.koumakan.rpc.commons.CryptoContext.decryptMap;


@ChannelHandler.Sharable
public abstract class AesDecoder extends MessageToMessageDecoder<ByteBuf> {

    private boolean isServer;

    protected AesDecoder(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Cipher cipher;
        Channel channel;

        if (isServer) {
            channel = ctx.channel().parent();
        } else {
            channel = ctx.channel();
        }

        cipher = decryptMap.get(channel);

        if (cipher != null) {
            byte[] ciphertext = new byte[in.readableBytes()];
            in.readBytes(ciphertext);
            byte[] plaintext = cipher.doFinal(ciphertext);
            ByteBuf byteBuf = ctx.alloc().buffer().writeBytes(plaintext);
            out.add(byteBuf);
        }
    }
}
