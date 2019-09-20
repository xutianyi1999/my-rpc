package club.koumakan.rpc;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ChannelFutureContainer implements ChannelFutureListener {

    private Future future;

    public ChannelFutureContainer(Future future) {
        this.future = future;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) {
        future.execute(channelFuture.cause(), null);
    }
}
