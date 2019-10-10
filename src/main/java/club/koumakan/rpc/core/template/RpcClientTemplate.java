package club.koumakan.rpc.core.template;

import club.koumakan.rpc.core.Future;
import club.koumakan.rpc.core.channel.Sender;
import club.koumakan.rpc.core.client.ConnectConfig;
import club.koumakan.rpc.core.client.ConnectHandler;
import io.netty.bootstrap.Bootstrap;

public class RpcClientTemplate {

    private Bootstrap bootstrap;

    public RpcClientTemplate(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void connect(ConnectConfig connectConfig, Future<Sender> future) {
        new ConnectHandler(bootstrap, connectConfig, future).connect();
    }
}
