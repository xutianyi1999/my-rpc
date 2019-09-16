package club.koumakan.rpc.channel;

import club.koumakan.rpc.Future;
import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.message.entity.Call;
import io.netty.channel.Channel;

import static club.koumakan.rpc.RpcContext.callbackMap;


public class Sender {

    private Channel channel;

    public Sender(Channel channel) {
        this.channel = channel;
    }

    public void send(Object requestMessage, Callback callback) {
        Call call = new Call(requestMessage);
        channel.writeAndFlush(call).addListener(callback);
        callbackMap.put(call.CALL_ID, callback);
    }

    public void close(Future future) {
        channel.close().addListener(future);
    }
}
