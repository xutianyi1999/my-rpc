package club.koumakan.rpc.server;

import club.koumakan.rpc.channel.Receiver;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public abstract class BindFuture implements GenericFutureListener<ChannelFuture> {

    public abstract void operationComplete(boolean isSuccess, Throwable throwable, Receiver receiver);

    @Override
    public void operationComplete(ChannelFuture channelFuture) {
        operationComplete(channelFuture.isSuccess(), channelFuture.cause(), new Receiver(channelFuture.channel()));
    }
}
