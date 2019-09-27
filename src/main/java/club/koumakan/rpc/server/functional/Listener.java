package club.koumakan.rpc.server.functional;

import club.koumakan.rpc.server.Channel;

@FunctionalInterface
public interface Listener<T> {

    void read(T requestMessage, Channel channel);
}
