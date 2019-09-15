package club.koumakan.rpc;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public abstract class Future implements GenericFutureListener<ChannelFuture> {

    public abstract void operationComplete(boolean isSuccess, Throwable throwable);

    @Override
    public void operationComplete(ChannelFuture channelFuture) {
        operationComplete(channelFuture.isSuccess(), channelFuture.cause());
    }
}
