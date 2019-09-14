package club.koumakan.rpc.client;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public interface Callback<c> extends GenericFutureListener<ChannelFuture> {

    void response(c responseMessage);
}
