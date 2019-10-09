package club.koumakan.rpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;

import static club.koumakan.rpc.commons.CryptoContext.DELIMITER;
import static club.koumakan.rpc.commons.CryptoContext.encryptMap;

@ChannelHandler.Sharable
public class AesEncoder extends MessageToByteEncoder<ByteBuf> {

    private boolean isServer;

    private static AesEncoder serverAesEncoder;

    private static AesEncoder clientAesEncoder;

    private AesEncoder(boolean isServer) {
        this.isServer = isServer;
    }

    public static AesEncoder getInstance(boolean isServer) {
        if (isServer) {
            if (serverAesEncoder == null) {
                serverAesEncoder = new AesEncoder(true);
            }
            return serverAesEncoder;
        } else {
            if (clientAesEncoder == null) {
                clientAesEncoder = new AesEncoder(false);
            }
            return clientAesEncoder;
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        Cipher cipher;
        Channel channel;

        if (isServer) {
            channel = ctx.channel().parent();
        } else {
            channel = ctx.channel();
        }

        cipher = encryptMap.get(channel);

        if (cipher != null) {
            byte[] plaintext = new byte[msg.readableBytes()];
            msg.readBytes(plaintext);
            byte[] ciphertext = cipher.doFinal(plaintext);
            out.writeBytes(ciphertext);
            out.writeLong(DELIMITER);
        }
    }
}
