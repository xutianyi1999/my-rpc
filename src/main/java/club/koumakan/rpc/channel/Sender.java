package club.koumakan.rpc.channel;

import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.message.entity.RequestMessage;
import io.netty.channel.Channel;

import static club.koumakan.rpc.RpcContext.callbackMap;


public class Sender {

    private Channel channel;

    public Sender(Channel channel) {
        this.channel = channel;
    }

    public void send(RequestMessage requestMessage, Callback callback) {
        channel.writeAndFlush(requestMessage).addListener(callback);
        callbackMap.put(requestMessage.getCallId(), callback);
    }

    public void close() throws InterruptedException {
        channel.close().sync();
    }
}
