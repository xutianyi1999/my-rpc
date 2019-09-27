package club.koumakan.rpc.channel;

import club.koumakan.rpc.ChannelFutureContainer;
import club.koumakan.rpc.Future;
import club.koumakan.rpc.commons.CryptoContext;
import club.koumakan.rpc.server.Listener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static club.koumakan.rpc.commons.ServerContext.listenerMap;


public class Receiver {

    private Channel channel;
    private InetSocketAddress inetSocketAddress;

    public Receiver(Channel channel) {
        this.channel = channel;
        this.inetSocketAddress = (InetSocketAddress) channel.localAddress();
    }

    public void receive(String functionCode, Listener listener) {
        Map<String, Listener> functionMap = listenerMap.get(inetSocketAddress.getPort());

        if (functionMap == null) {
            functionMap = new ConcurrentHashMap<>();
            functionMap.put(functionCode, listener);
            listenerMap.put(inetSocketAddress.getPort(), functionMap);
        } else {
            functionMap.put(functionCode, listener);
        }
    }

    public void close(Future future) {
        listenerMap.remove(inetSocketAddress.getPort());
        CryptoContext.removeCipher(channel);
        ChannelFuture channelFuture = channel.close();

        if (future != null) {
            channelFuture.addListener(new ChannelFutureContainer(future));
        }
    }

    public void close() {
        close(null);
    }
}
