package club.koumakan.rpc.core.server.functional;

import club.koumakan.rpc.core.server.Channel;

@FunctionalInterface
public interface Listener<T> {

    void read(T requestMessage, Channel channel);
}
