package club.koumakan.rpc;

public interface Future<T> {

    void execute(Throwable throwable, T object);
}
