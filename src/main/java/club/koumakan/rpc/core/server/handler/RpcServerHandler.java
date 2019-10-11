package club.koumakan.rpc.core.server.handler;

import club.koumakan.rpc.core.message.Call;
import club.koumakan.rpc.core.server.Channel;
import club.koumakan.rpc.core.server.functional.Listener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;
import java.util.Map;

import static club.koumakan.rpc.core.server.ServerContext.listenerMap;


@ChannelHandler.Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler<Call> {

    public static final RpcServerHandler INSTANCE = new RpcServerHandler();

    private RpcServerHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Call msg) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().localAddress();
        Map<String, Listener> functionMap = listenerMap.get(inetSocketAddress.getPort());

        if (functionMap != null) {
            Listener listener = functionMap.get(msg.getFunctionCode());

            if (listener != null) {
                listener.read(msg.getData(), new Channel(ctx, msg));
            } else {
                System.out.println("Function not matched: " + msg.getFunctionCode());
            }
        }
    }
}
