package club.koumakan.rpc.client;

public interface Callback<T> {

    void response(T object);
}
