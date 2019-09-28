package club.koumakan.rpc.template;

import club.koumakan.rpc.Future;
import club.koumakan.rpc.channel.Sender;
import club.koumakan.rpc.client.ConnectConfig;
import club.koumakan.rpc.client.ConnectHandler;
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
