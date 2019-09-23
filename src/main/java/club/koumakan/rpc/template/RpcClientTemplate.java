package club.koumakan.rpc.template;

import club.koumakan.rpc.Future;
import club.koumakan.rpc.channel.Sender;
import club.koumakan.rpc.commons.EncryptContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;

public class RpcClientTemplate {

    private Bootstrap bootstrap;

    public RpcClientTemplate(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void connect(String ipAddress, int port, Future<Sender> future) {
        bootstrap.connect(ipAddress, port).addListener((GenericFutureListener<ChannelFuture>) channelFuture ->
                future.execute(channelFuture.cause(), new Sender(channelFuture.channel())));
    }

    public void connect(String ipAddress, int port, String key, Future<Sender> future) {
        bootstrap.connect(ipAddress, port).addListener((GenericFutureListener<ChannelFuture>) channelFuture -> {
            if (channelFuture.cause() != null) {
                future.execute(channelFuture.cause(), null);
            } else {
                Channel channel = channelFuture.channel();
                EncryptContext.addCipher(key, (InetSocketAddress) channel.remoteAddress());
                future.execute(null, new Sender(channel));
            }
        });
    }
}
