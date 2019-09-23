package club.koumakan.rpc.handler;

import club.koumakan.rpc.commons.Context;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import javax.crypto.Cipher;
import java.net.InetSocketAddress;
import java.util.List;

import static club.koumakan.rpc.commons.Context.decryptMap;


@ChannelHandler.Sharable
public class AesDecoder extends MessageToMessageDecoder<ByteBuf> {

    private boolean isServer;

    public AesDecoder(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Cipher cipher;
        InetSocketAddress inetSocketAddress;

        if (isServer) {
            inetSocketAddress = (InetSocketAddress) ctx.channel().localAddress();
        } else {
            inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        }

        cipher = decryptMap.get(Context.translateMapKey(inetSocketAddress));

        if (cipher != null) {
            byte[] ciphertext = new byte[in.readableBytes()];
            in.readBytes(ciphertext);
            byte[] plaintext = cipher.doFinal(ciphertext);
            ByteBuf byteBuf = ctx.alloc().buffer().writeBytes(plaintext);
            out.add(byteBuf);
        }
    }
}
