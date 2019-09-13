package club.koumakan.rpc.handler;

import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.message.entity.ResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static club.koumakan.rpc.RpcContext.callbackMap;

@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<ResponseMessage> {

    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) {
        Callback callback = callbackMap.get(msg.getCallId());

        if (callback != null) {
            callbackMap.remove(msg.getCallId());
            callback.response(msg);
        }
    }
}
