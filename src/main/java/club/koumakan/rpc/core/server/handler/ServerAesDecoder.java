package club.koumakan.rpc.core.server.handler;

import club.koumakan.rpc.core.handler.AesDecoder;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class ServerAesDecoder extends AesDecoder {

    public static final ServerAesDecoder INSTANCE = new ServerAesDecoder();

    private ServerAesDecoder() {
        super(true);
    }
}
