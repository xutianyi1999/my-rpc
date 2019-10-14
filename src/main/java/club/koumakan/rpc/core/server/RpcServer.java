package club.koumakan.rpc.core.server;

import club.koumakan.rpc.core.Future;
import club.koumakan.rpc.core.RpcCore;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class RpcServer {

    private ServerBootstrap serverBootstrap;
    private RpcCore rpcCore;

    public RpcServer(ServerBootstrap serverBootstrap, RpcCore rpcCore) {
        this.serverBootstrap = serverBootstrap;
        this.rpcCore = rpcCore;
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

    public void destroy() {
        rpcCore.destroy();
        ServerContext.listenerMap.clear();
    }
}
