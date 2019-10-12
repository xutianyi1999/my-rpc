package club.koumakan.rpc.core.client.handler;

import club.koumakan.rpc.core.client.CallbackInfo;
import club.koumakan.rpc.core.client.functional.Inactive;
import club.koumakan.rpc.core.message.Call;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;

import static club.koumakan.rpc.core.client.ClientContext.callbackMap;
import static club.koumakan.rpc.core.client.ClientContext.inactiveMap;


@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<Call> {

    public static final RpcClientHandler INSTANCE = new RpcClientHandler();

    private RpcClientHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Call msg) {
        CallbackInfo callbackInfo = callbackMap.remove(msg.CALL_ID);

        if (callbackInfo != null) {
            callbackInfo.getCallback().response(null, msg.getData());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        Inactive inactive = inactiveMap.remove(channel.id().asShortText());

        if (inactive != null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
            inactive.execute(inetSocketAddress);
        }
    }
}
