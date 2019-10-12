package club.koumakan.rpc.core.server;

import club.koumakan.rpc.core.ChannelFutureContainer;
import club.koumakan.rpc.core.Future;
import club.koumakan.rpc.core.server.functional.Listener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static club.koumakan.rpc.core.server.ServerContext.listenerMap;


public class Receiver {

    private Channel channel;
    private InetSocketAddress inetSocketAddress;

    public Receiver(Channel channel) {
        this.channel = channel;
        this.inetSocketAddress = (InetSocketAddress) channel.localAddress();
    }

    public Receiver publish(String functionCode, Listener listener) {
        Map<String, Listener> functionMap = listenerMap.get(inetSocketAddress.getPort());

        if (functionMap == null) {
            functionMap = new ConcurrentHashMap<>();
            functionMap.put(functionCode, listener);
            listenerMap.put(inetSocketAddress.getPort(), functionMap);
        } else {
            functionMap.put(functionCode, listener);
        }
        return this;
    }

    public void close(Future future) {
        listenerMap.remove(inetSocketAddress.getPort());
        ChannelFuture channelFuture = channel.close();

        if (future != null) {
            channelFuture.addListener(new ChannelFutureContainer(future));
        }
    }

    public void close() {
        close(null);
    }
}
