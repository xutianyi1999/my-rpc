package club.koumakan.rpc.client.functional;

import club.koumakan.rpc.client.ReconnectHandler;

import java.net.InetSocketAddress;

@FunctionalInterface
public interface Inactive {

    void execute(InetSocketAddress remoteAddress, ReconnectHandler reconnectHandler);
}
