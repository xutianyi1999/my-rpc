package club.koumakan.rpc.client;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public interface Callback<C> extends GenericFutureListener<ChannelFuture> {

    void response(C responseMessage);
}
