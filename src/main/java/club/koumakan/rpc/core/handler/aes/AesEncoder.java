package club.koumakan.rpc.core.handler.aes;

import club.koumakan.rpc.core.commons.CryptoUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;

@ChannelHandler.Sharable
public class AesEncoder extends MessageToByteEncoder<ByteBuf> {

    private Cipher cipher;

    public AesEncoder(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        byte[] plaintext = new byte[msg.readableBytes()];
        msg.readBytes(plaintext);
        byte[] ciphertext = cipher.doFinal(plaintext);
        out.writeBytes(ciphertext).writeBytes(CryptoUtils.DELIMITER);
    }
}
