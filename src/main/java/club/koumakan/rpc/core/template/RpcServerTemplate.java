package club.koumakan.rpc.core.template;

import club.koumakan.rpc.core.Future;
import club.koumakan.rpc.core.channel.Receiver;
import club.koumakan.rpc.core.commons.CryptoUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class RpcServerTemplate {

    private ServerBootstrap serverBootstrap;

    public RpcServerTemplate(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    public void bind(int port, Future<Receiver> future) {
        serverBootstrap.bind(port).addListener((GenericFutureListener<ChannelFuture>) channelFuture -> {
            if (channelFuture.cause() != null) {
                future.execute(channelFuture.cause(), null);
            } else {
                future.execute(null, new Receiver(channelFuture.channel()));
            }
        });
    }

    public void bind(int port, String key, Future<Receiver> future) {
        serverBootstrap.bind(port).addListener((GenericFutureListener<ChannelFuture>) channelFuture -> {
            if (channelFuture.cause() != null) {
                future.execute(channelFuture.cause(), null);
            } else {
                Channel channel = channelFuture.channel();
                CryptoUtils.addCipher(key, channel);
                future.execute(null, new Receiver(channel));
            }
        });
    }
}
