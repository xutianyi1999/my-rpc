package club.koumakan.rpc.template;

import club.koumakan.rpc.Future;
import club.koumakan.rpc.channel.Sender;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;

public class RpcClientTemplate {

    private Bootstrap bootstrap;

    public RpcClientTemplate(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void connect(String ipAddress, int port, Future<Sender> future) {
        bootstrap.connect(ipAddress, port).addListener((ChannelFutureListener) channelFuture ->
                future.execute(channelFuture.cause(), new Sender(channelFuture.channel())));
    }
}
