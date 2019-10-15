package club.koumakan.rpc.core.client;

import club.koumakan.rpc.core.ChannelFutureContainer;
import club.koumakan.rpc.core.Future;
import club.koumakan.rpc.core.client.functional.Callback;
import club.koumakan.rpc.core.client.functional.Inactive;
import club.koumakan.rpc.core.exception.CallbackTimeoutException;
import club.koumakan.rpc.core.message.Call;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


public class Sender {

    private Channel channel;
    private String channelId;

    public Sender(Channel channel) {
        this.channel = channel;
        this.channelId = channel.id().asShortText();
    }

    public Sender send(String functionCode, Object requestMessage, Callback callback) {
        return send(functionCode, requestMessage, callback, 10000);
    }

    public Sender send(String functionCode, Object requestMessage, Callback callback, int callbackTimeout) {
        Call call = new Call(requestMessage, functionCode);
        CallbackInfo callbackInfo = new CallbackInfo(callback);
        ClientContext.callbackMap.put(call.CALL_ID, callbackInfo);

        channel.writeAndFlush(call).addListener(channelFuture -> {
            Throwable throwable = channelFuture.cause();

            if (throwable != null) {
                ClientContext.callbackMap.remove(call.CALL_ID);
                callback.response(throwable, null);
            } else {
                channel.eventLoop().schedule(() -> {
                    if (!callbackInfo.isCall()) {
                        ClientContext.callbackMap.remove(call.CALL_ID);
                        callback.response(new CallbackTimeoutException(), null);
                    }
                }, callbackTimeout, TimeUnit.MILLISECONDS);
            }
        });
        return this;
    }

    public void close(Future future) {
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

    public Sender addListenerInactive(Inactive inactive) {
        ClientContext.inactiveMap.put(channelId, inactive);
        return this;
    }

    public Channel getChannel() {
        return channel;
    }
}
