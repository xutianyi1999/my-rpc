package club.koumakan.rpc.handler.aes;

import club.koumakan.rpc.handler.AesEncoder;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class ClientAesEncoder extends AesEncoder {

    public static final ClientAesEncoder INSTANCE = new ClientAesEncoder();

    private ClientAesEncoder() {
        super(false);
    }
}
