package club.koumakan.rpc.server;

import club.koumakan.rpc.message.entity.Call;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;

public class Channel {

    private ChannelHandlerContext ctx;

    private Call call;

    public Channel(ChannelHandlerContext ctx, Call call) {
        this.ctx = ctx;
        this.call = call;
    }

    public void response(Object responseMessage, GenericFutureListener<ChannelFuture> genericFutureListener) {
        call.setData(responseMessage);
        ctx.writeAndFlush(call).addListener(genericFutureListener);
    }
}
