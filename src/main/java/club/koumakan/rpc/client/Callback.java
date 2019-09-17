package club.koumakan.rpc.client;

@FunctionalInterface
public interface Callback<T> {

    void response(T responseMessage);
}
