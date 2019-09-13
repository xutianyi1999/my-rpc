package club.koumakan.rpc.handler;

import club.koumakan.rpc.message.entity.RequestMessage;
import club.koumakan.rpc.server.Channel;
import club.koumakan.rpc.server.Listener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;

import static club.koumakan.rpc.RpcContext.listenerMap;

@ChannelHandler.Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler<RequestMessage> {

    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().localAddress();
        Listener listener = listenerMap.get(inetSocketAddress.getPort());

        if (listener != null) {
            listener.read(msg, new Channel(ctx));
        }
    }
}
