package club.koumakan.rpc.template;

import club.koumakan.rpc.server.BindFuture;
import io.netty.bootstrap.ServerBootstrap;

public class RpcServerTemplate {

    private ServerBootstrap serverBootstrap;

    public RpcServerTemplate(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    public void bind(int port, BindFuture bindFuture) {
        serverBootstrap.bind(port).addListener(bindFuture);
    }
}
