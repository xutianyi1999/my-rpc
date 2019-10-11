package club.koumakan.rpc.core.client.handler;

import club.koumakan.rpc.core.handler.AesEncoder;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class ClientAesEncoder extends AesEncoder {

    public static final ClientAesEncoder INSTANCE = new ClientAesEncoder();

    private ClientAesEncoder() {
        super(false);
    }
}
