package club.koumakan.rpc.handler;

import club.koumakan.rpc.commons.EncryptContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;
import java.net.InetSocketAddress;

import static club.koumakan.rpc.commons.EncryptContext.RANDOM_VALUE;
import static club.koumakan.rpc.commons.EncryptContext.encryptMap;

@ChannelHandler.Sharable
public class AesEncoder extends MessageToByteEncoder<ByteBuf> {

    private boolean isServer;

    public AesEncoder(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        Cipher cipher;
        InetSocketAddress inetSocketAddress;

        if (isServer) {
            inetSocketAddress = (InetSocketAddress) ctx.channel().localAddress();
        } else {
            inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        }

        cipher = encryptMap.get(EncryptContext.translateMapKey(inetSocketAddress));

        if (cipher != null) {
            byte[] plaintext = new byte[msg.readableBytes()];
            msg.readBytes(plaintext);
            byte[] ciphertext = cipher.doFinal(plaintext);
            out.writeBytes(ciphertext);
            out.writeLong(RANDOM_VALUE);
        }
    }
}
