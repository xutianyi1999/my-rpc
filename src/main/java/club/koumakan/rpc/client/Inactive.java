package club.koumakan.rpc.client;

import java.net.InetSocketAddress;

@FunctionalInterface
public interface Inactive {

    void execute(InetSocketAddress remoteAddress);
}
