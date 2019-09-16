package club.koumakan.rpc.template;

import club.koumakan.rpc.client.ConnectFuture;
import io.netty.bootstrap.Bootstrap;

public class RpcClientTemplate {

    private Bootstrap bootstrap;

    public RpcClientTemplate(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void connect(String ipAddress, int port, ConnectFuture connectFuture) {
        bootstrap.connect(ipAddress, port).addListener(connectFuture);
    }
}
