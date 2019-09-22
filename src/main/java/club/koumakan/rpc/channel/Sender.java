package club.koumakan.rpc.channel;

import club.koumakan.rpc.ChannelFutureContainer;
import club.koumakan.rpc.Future;
import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.client.Inactive;
import club.koumakan.rpc.message.entity.Call;
import io.netty.channel.Channel;

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
        channel.writeAndFlush(call).addListener(new ChannelFutureContainer(future));
        callbackMap.put(call.CALL_ID, callback);
    }

    public void close(Future future) {
        channel.close().addListener(new ChannelFutureContainer(future));
        inactiveMap.remove(channel);
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
