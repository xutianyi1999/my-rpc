package club.koumakan.rpc;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class ChannelFutureContainer implements GenericFutureListener<ChannelFuture> {

    private Future future;

    public ChannelFutureContainer(Future future) {
        this.future = future;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) {
        future.execute(channelFuture.cause(), null);
    }
}
