package club.koumakan.rpc.core.client;

import club.koumakan.rpc.core.Future;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

public class ConnectHandler {

    private Bootstrap bootstrap;
    private ConnectConfig connectConfig;
    private Future<Sender> future;
    private int retries;
    private final GenericFutureListener<ChannelFuture> GENERIC_FUTURE_LISTENER = channelFuture -> {
        Channel channel = channelFuture.channel();

        if (channelFuture.isSuccess()) {
            future.execute(null, new Sender(channel));
        } else {
            if (retries != 0) {
                retries--;
                channelFuture.cause().printStackTrace();
                channel.eventLoop().schedule(connectTask, connectConfig.getSleepMs(), TimeUnit.MILLISECONDS);
            } else {
                future.execute(channelFuture.cause(), null);
            }
        }
    };
    private final Runnable connectTask = this::connect;

    public ConnectHandler(Bootstrap bootstrap, ConnectConfig connectConfig, Future<Sender> future) {
        this.bootstrap = bootstrap;
        this.connectConfig = connectConfig;
        this.future = future;
        this.retries = connectConfig.getRetries();
    }

    public void connect() {
        bootstrap.connect(connectConfig.getIpAddress(), connectConfig.getPort()).addListener(GENERIC_FUTURE_LISTENER);
    }
}
