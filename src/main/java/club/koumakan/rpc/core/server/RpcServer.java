package club.koumakan.rpc.core.server;

import club.koumakan.rpc.core.Future;
import club.koumakan.rpc.core.commons.CryptoUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class RpcServer {

    private ServerBootstrap serverBootstrap;

    public RpcServer(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    public RpcServer bind(int port, Future<Receiver> future) {
        return bind(port, null, future);
    }

    public RpcServer bind(int port, String key, Future<Receiver> future) {
        serverBootstrap.bind(port).addListener((GenericFutureListener<ChannelFuture>) channelFuture -> {
            if (channelFuture.cause() != null) {
                future.execute(channelFuture.cause(), null);
            } else {
                Channel channel = channelFuture.channel();

                if (key != null) {
                    CryptoUtils.addCipher(key, channel);
                }
                future.execute(null, new Receiver(channel));
            }
        });
        return this;
    }
}
