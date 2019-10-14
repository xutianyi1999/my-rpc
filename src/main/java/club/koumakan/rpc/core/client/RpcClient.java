package club.koumakan.rpc.core.client;

import club.koumakan.rpc.core.Future;
import club.koumakan.rpc.core.RpcCore;
import io.netty.bootstrap.Bootstrap;

public class RpcClient {

    private Bootstrap bootstrap;
    private RpcCore rpcCore;

    public RpcClient(Bootstrap bootstrap, RpcCore rpcCore) {
        this.bootstrap = bootstrap;
        this.rpcCore = rpcCore;
    }

    public RpcClient connect(ConnectConfig connectConfig, Future<Sender> future) {
        new ConnectHandler(bootstrap, connectConfig, future).connect();
        return this;
    }

    public void destroy() {
        rpcCore.destroy();
        ClientContext.callbackMap.clear();
        ClientContext.inactiveMap.clear();
    }
}
