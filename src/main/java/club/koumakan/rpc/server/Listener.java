package club.koumakan.rpc.server;

@FunctionalInterface
public interface Listener<T> {

    void read(T requestMessage, Channel channel);
}
