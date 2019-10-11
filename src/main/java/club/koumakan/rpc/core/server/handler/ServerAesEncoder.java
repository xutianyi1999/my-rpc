package club.koumakan.rpc.core.server.handler;

import club.koumakan.rpc.core.handler.AesEncoder;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class ServerAesEncoder extends AesEncoder {

    public static final ServerAesEncoder INSTANCE = new ServerAesEncoder();

    private ServerAesEncoder() {
        super(true);
    }
}
