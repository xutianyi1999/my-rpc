package club.koumakan.rpc.server;

public interface Listener<c> {

    void read(c requestMessage, Channel channel);
}
