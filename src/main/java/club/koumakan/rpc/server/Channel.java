package club.koumakan.rpc.server;

import club.koumakan.rpc.Future;
import club.koumakan.rpc.message.entity.Call;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class Channel {

    private ChannelHandlerContext ctx;

    private Call call;

    public Channel(ChannelHandlerContext ctx, Call call) {
        this.ctx = ctx;
        this.call = call;
    }

    public void response(Object responseMessage, Future<?> future) {
        call.setData(responseMessage);
        ctx.writeAndFlush(call).addListener((ChannelFutureListener) channelFuture -> {
            future.execute(channelFuture.cause(), null);
        });
    }
}
