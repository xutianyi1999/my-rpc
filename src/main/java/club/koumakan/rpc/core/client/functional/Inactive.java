package club.koumakan.rpc.core.client.functional;

import java.net.InetSocketAddress;

@FunctionalInterface
public interface Inactive {

    void execute(InetSocketAddress remoteAddress);
}
