package club.koumakan.rpc.core.server;

import club.koumakan.rpc.core.ChannelFutureContainer;
import club.koumakan.rpc.core.Future;
import club.koumakan.rpc.core.exception.ResponseException;
import club.koumakan.rpc.core.message.Call;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class Channel {

    private static final ResponseException RESPONSE_EXCEPTION = new ResponseException("Has been sent");

    private ChannelHandlerContext ctx;
    private Call call;
    private boolean flag = false;

    public Channel(ChannelHandlerContext ctx, Call call) {
        this.ctx = ctx;
        this.call = call;
    }

    public Channel response(Object responseMessage, Future future) throws ResponseException {
        if (flag) {
            throw RESPONSE_EXCEPTION;
        } else {
            flag = true;
        }

        call.setData(responseMessage);
        ChannelFuture channelFuture = ctx.writeAndFlush(call);

        if (future != null) {
            channelFuture.addListener(new ChannelFutureContainer(future));
        }
        return this;
    }

    public Channel response(Object responseMessage) throws ResponseException {
        return response(responseMessage, null);
    }

    public void close(Future future) {
        ChannelFuture channelFuture = ctx.close();

        if (future != null) {
            channelFuture.addListener(new ChannelFutureContainer(future));
        }
    }

    public void close() {
        close(null);
    }
}
