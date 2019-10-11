package club.koumakan.rpc.core.client;

import club.koumakan.rpc.core.ChannelFutureContainer;
import club.koumakan.rpc.core.Future;
import club.koumakan.rpc.core.client.functional.Callback;
import club.koumakan.rpc.core.client.functional.Inactive;
import club.koumakan.rpc.core.message.Call;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;


public class Sender {

    private Channel channel;

    public Sender(Channel channel) {
        this.channel = channel;
    }

    public void send(String functionCode, Object requestMessage, Callback callback) {
        Call call = new Call(requestMessage, functionCode);
        ClientContext.callbackMap.put(call.CALL_ID, callback);

        channel.writeAndFlush(call).addListener(channelFuture -> {
            Throwable throwable = channelFuture.cause();

            if (throwable != null) {
                ClientContext.callbackMap.remove(call.CALL_ID);
                callback.response(throwable, null);
            }
        });
    }

    public void close(Future future) {
        ClientContext.inactiveMap.remove(channel);
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
        ClientContext.inactiveMap.put(channel, inactive);
    }
}
