package club.koumakan.rpc.server;

public interface Listener<C> {

    void read(C requestMessage, Channel channel);
}
