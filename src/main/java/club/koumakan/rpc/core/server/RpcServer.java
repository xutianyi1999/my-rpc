package club.koumakan.rpc.core.server;

import club.koumakan.rpc.core.Future;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class RpcServer {

    private ServerBootstrap serverBootstrap;

    public RpcServer(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    public RpcServer bind(int port, Future<Receiver> future) {
        serverBootstrap.bind(port).addListener((GenericFutureListener<ChannelFuture>) channelFuture -> {
            if (channelFuture.cause() != null) {
                future.execute(channelFuture.cause(), null);
            } else {
                future.execute(null, new Receiver(channelFuture.channel()));
            }
        });
        return this;
    }
}
