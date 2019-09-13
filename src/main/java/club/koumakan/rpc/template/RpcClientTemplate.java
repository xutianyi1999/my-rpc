package club.koumakan.rpc.template;

import club.koumakan.rpc.channel.Sender;
import io.netty.bootstrap.Bootstrap;

public class RpcClientTemplate {

    private Bootstrap bootstrap;

    public RpcClientTemplate(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public Sender connect(String ipAddress, int port) throws InterruptedException {
        return new Sender(bootstrap.connect(ipAddress, port).sync().channel());
    }
}
