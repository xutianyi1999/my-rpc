package club.koumakan.rpc.channel;

import club.koumakan.rpc.ChannelFutureContainer;
import club.koumakan.rpc.Future;
import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.client.Inactive;
import club.koumakan.rpc.message.entity.Call;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;

import static club.koumakan.rpc.commons.ClientContext.callbackMap;
import static club.koumakan.rpc.commons.ClientContext.inactiveMap;


public class Sender {

    private Channel channel;

    public Sender(Channel channel) {
        this.channel = channel;
    }

    public void send(String functionCode, Object requestMessage, Future future, Callback callback) {
        Call call = new Call(requestMessage, functionCode);

        channel.writeAndFlush(call).addListener((ChannelFutureListener) channelFuture -> {
            Throwable throwable = channelFuture.cause();

            if (throwable == null) {
                callbackMap.put(call.CALL_ID, callback);
            }
            future.execute(throwable, null);
        });
    }

    public void close(Future future) {
        inactiveMap.remove(channel);
        ChannelFuture channelFuture = channel.close();

        if (future != null) {
            channelFuture.addListener(new ChannelFutureContainer(future));
        }
    }

    public void close() {
        close(null);
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

    public void addListenerInactive(Inactive inactive) {
        inactiveMap.put(channel, inactive);
    }
}
