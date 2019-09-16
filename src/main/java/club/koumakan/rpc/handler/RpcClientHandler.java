package club.koumakan.rpc.handler;

import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.client.ListenerInactive;
import club.koumakan.rpc.message.entity.Call;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutor;

import java.net.InetSocketAddress;

import static club.koumakan.rpc.RpcContext.callbackMap;
import static club.koumakan.rpc.RpcContext.inactiveMap;

@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<Call> {

    protected void channelRead0(ChannelHandlerContext ctx, Call msg) {
        EventExecutor executor = ctx.executor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        });

        Callback callback = callbackMap.get(msg.CALL_ID);

        if (callback != null) {
            callbackMap.remove(msg.CALL_ID);
            callback.response(msg.getData());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (inactiveMap.size() > 0) {
            Channel channel = ctx.channel();
            ListenerInactive listenerInactive = inactiveMap.get(channel);

            if (listenerInactive != null) {
                inactiveMap.remove(channel);
                listenerInactive.inactive((InetSocketAddress) channel.remoteAddress());

                if (listenerInactive.isAutoReconnect()) {
                    EventExecutor executor = ctx.executor();
//                    executor.
                }
            }
        }
    }
}
