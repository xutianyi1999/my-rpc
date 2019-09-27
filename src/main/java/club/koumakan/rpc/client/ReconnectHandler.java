package club.koumakan.rpc.client;

import club.koumakan.rpc.client.functional.ReconnectListener;
import club.koumakan.rpc.template.RpcClientTemplate;

import java.net.InetSocketAddress;

import static club.koumakan.rpc.commons.ClientContext.reconnectListenerMap;

public class ReconnectHandler {

    private InetSocketAddress inetSocketAddress;

    public ReconnectHandler(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    public void add(RpcClientTemplate rpcClientTemplate, ReconnectListener reconnectListener) {
        reconnectListenerMap.put(inetSocketAddress, new ReconnectListenerEntity(reconnectListener, rpcClientTemplate));
    }
}
