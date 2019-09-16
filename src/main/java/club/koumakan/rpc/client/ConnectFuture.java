package club.koumakan.rpc.client;

import club.koumakan.rpc.channel.Sender;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public abstract class ConnectFuture implements GenericFutureListener<ChannelFuture> {

    public abstract void operationComplete(boolean isSuccess, Throwable throwable, Sender sender);

    @Override
    public void operationComplete(ChannelFuture channelFuture) {
        operationComplete(channelFuture.isSuccess(), channelFuture.cause(), new Sender(channelFuture.channel()));
    }
}
