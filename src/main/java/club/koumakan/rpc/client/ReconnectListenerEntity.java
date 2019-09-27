package club.koumakan.rpc.client;

import club.koumakan.rpc.client.functional.ReconnectListener;
import club.koumakan.rpc.template.RpcClientTemplate;

public class ReconnectListenerEntity {

    private ReconnectListener reconnectListener;

    private RpcClientTemplate rpcClientTemplate;

    public ReconnectListenerEntity(ReconnectListener reconnectListener, RpcClientTemplate rpcClientTemplate) {
        this.reconnectListener = reconnectListener;
        this.rpcClientTemplate = rpcClientTemplate;
    }

    public ReconnectListener getReconnectListener() {
        return reconnectListener;
    }

    public RpcClientTemplate getRpcClientTemplate() {
        return rpcClientTemplate;
    }
}
