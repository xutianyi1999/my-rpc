package club.koumakan.rpc.core.client.functional;

@FunctionalInterface
public interface Callback<T> {

    void response(Throwable throwable, T responseMessage);
}
