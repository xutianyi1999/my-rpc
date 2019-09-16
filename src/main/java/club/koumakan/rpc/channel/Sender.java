package club.koumakan.rpc.channel;

import club.koumakan.rpc.Future;
import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.message.entity.Call;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;

import static club.koumakan.rpc.RpcContext.callbackMap;


public class Sender {

    private Channel channel;

    public Sender(Channel channel) {
        this.channel = channel;
    }

    public void send(Object requestMessage, Future<?> future, Callback<?> callback) {
        Call call = new Call(requestMessage);

        channel.writeAndFlush(call).addListener((ChannelFutureListener) channelFuture ->
                future.execute(channelFuture.cause(), null));

        callbackMap.put(call.CALL_ID, callback);
    }

    public void close(Future<?> future) {
        channel.close().addListener((ChannelFutureListener) channelFuture ->
                future.execute(channelFuture.cause(), null));
    }

    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    public boolean isActive() {
        return channel.isActive();
    }

    public boolean isWriteable() {
        return channel.isWritable();
    }
}
