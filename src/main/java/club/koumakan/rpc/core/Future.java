package club.koumakan.rpc.core;

@FunctionalInterface
public interface Future<T> {

    void execute(Throwable throwable, T object);
}
