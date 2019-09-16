package club.koumakan.rpc.server;

public interface Listener<T> {

    void read(T requestMessage, Channel channel);
}
