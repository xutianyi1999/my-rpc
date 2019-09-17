package club.koumakan.rpc;

@FunctionalInterface
public interface Future<T> {

    void execute(Throwable throwable, T object);
}
