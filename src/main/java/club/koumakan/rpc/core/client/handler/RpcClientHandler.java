package club.koumakan.rpc.core.client.handler;

import club.koumakan.rpc.core.client.functional.Callback;
import club.koumakan.rpc.core.client.functional.Inactive;
import club.koumakan.rpc.core.commons.CryptoUtils;
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
        Callback callback = callbackMap.get(msg.CALL_ID);

        if (callback != null) {
            callbackMap.remove(msg.CALL_ID);
            callback.response(null, msg.getData());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        CryptoUtils.removeCipher(channel);
        Inactive inactive = inactiveMap.get(channel);

        if (inactive != null) {
            inactiveMap.remove(channel);
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
            inactive.execute(inetSocketAddress);
        }
    }
}