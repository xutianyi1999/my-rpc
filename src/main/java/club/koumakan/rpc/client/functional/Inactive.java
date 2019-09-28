package club.koumakan.rpc.client.functional;

import java.net.InetSocketAddress;

@FunctionalInterface
public interface Inactive {

    void execute(InetSocketAddress remoteAddress);
}
