package club.koumakan.rpc.client;

import club.koumakan.rpc.Future;
import club.koumakan.rpc.channel.Sender;
import club.koumakan.rpc.commons.CryptoUtils;
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
            if (connectConfig.getKey() != null) {
                CryptoUtils.addCipher(connectConfig.getKey(), channel);
            }
            future.execute(null, new Sender(channel));
        } else {

            if (retries != 0) {
                retries--;
                channelFuture.cause().printStackTrace();
                channel.eventLoop().schedule(this::connect, connectConfig.getSleepMs(), TimeUnit.MILLISECONDS);
            } else {
                future.execute(channelFuture.cause(), null);
            }
        }
    };

    public ConnectHandler(Bootstrap bootstrap, ConnectConfig connectConfig, Future<Sender> future) {
        this.bootstrap = bootstrap;
        this.connectConfig = connectConfig;
        this.future = future;
        this.retries = connectConfig.getRetries();
    }

    public void execute() {
        connect();
    }

    private void connect() {
        bootstrap.connect(connectConfig.getIpAddress(), connectConfig.getPort()).addListener(GENERIC_FUTURE_LISTENER);
    }
}
