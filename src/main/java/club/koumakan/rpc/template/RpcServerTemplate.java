package club.koumakan.rpc.template;

import club.koumakan.rpc.channel.Receiver;
import io.netty.bootstrap.ServerBootstrap;

public class RpcServerTemplate {

    private ServerBootstrap serverBootstrap;

    public RpcServerTemplate(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    public Receiver bind(int port) throws InterruptedException {
        return new Receiver(serverBootstrap.bind(port).sync().channel());
    }
}
