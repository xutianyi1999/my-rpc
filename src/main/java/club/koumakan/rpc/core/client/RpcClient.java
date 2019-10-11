package club.koumakan.rpc.core.client;

import club.koumakan.rpc.core.Future;
import io.netty.bootstrap.Bootstrap;

public class RpcClient {

    private Bootstrap bootstrap;

    public RpcClient(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void connect(ConnectConfig connectConfig, Future<Sender> future) {
        new ConnectHandler(bootstrap, connectConfig, future).connect();
    }
}
