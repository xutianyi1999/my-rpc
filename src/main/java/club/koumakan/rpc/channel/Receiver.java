package club.koumakan.rpc.channel;

import club.koumakan.rpc.Future;
import club.koumakan.rpc.server.Listener;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

import static club.koumakan.rpc.RpcContext.listenerMap;

public class Receiver {

    private Channel channel;
    private InetSocketAddress inetSocketAddress;

    public Receiver(Channel channel) {
        this.channel = channel;
        this.inetSocketAddress = (InetSocketAddress) channel.localAddress();
    }

    public void receive(Listener listener) {
        listenerMap.put(inetSocketAddress.getPort(), listener);
    }

    public void close(Future future) {
        listenerMap.remove(inetSocketAddress.getPort());
        channel.close().addListener(future);
    }
}
