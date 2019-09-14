package club.koumakan.rpc.handler;

import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.message.entity.Call;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static club.koumakan.rpc.RpcContext.callbackMap;

@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<Call> {

    protected void channelRead0(ChannelHandlerContext ctx, Call msg) {
        Callback callback = callbackMap.get(msg.CALL_ID);

        if (callback != null) {
            callbackMap.remove(msg.CALL_ID);
            callback.response(msg.getData());
        }
    }
}
