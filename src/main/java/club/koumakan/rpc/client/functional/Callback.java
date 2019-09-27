package club.koumakan.rpc.client.functional;

@FunctionalInterface
public interface Callback<T> {

    void response(T responseMessage);
}
