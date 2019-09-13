package club.koumakan.rpc.server;

import club.koumakan.rpc.message.entity.ResponseMessage;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;

public class Channel {

    private ChannelHandlerContext ctx;

    public Channel(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void response(ResponseMessage responseMessage, GenericFutureListener<ChannelFuture> genericFutureListener) {
        ctx.writeAndFlush(responseMessage).addListener(genericFutureListener);
    }
}
