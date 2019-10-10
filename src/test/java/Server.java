import club.koumakan.rpc.core.ClassResolverType;
import club.koumakan.rpc.core.RpcFactory;
import club.koumakan.rpc.core.server.functional.Listener;
import club.koumakan.rpc.core.template.RpcServerTemplate;

public class Server {

    public static void main(String[] args) {
        try {
            RpcFactory.initServer();
            RpcServerTemplate serverTemplate = RpcFactory.createServerTemplate(ClassResolverType.softCachingConcurrentResolver, true, true, false);
            serverTemplate.bind(19999, "123", (throwable, receiver) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    receiver.receive("test", (Listener<Long>) (requestMessage, channel) -> {
                        channel.response(requestMessage);
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
