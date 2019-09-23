import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.server.Listener;
import club.koumakan.rpc.template.RpcServerTemplate;

import static club.koumakan.rpc.ClassResolverType.weakCachingResolver;

public class Server {

    public static void main(String[] args) {
        try {
            RpcFactory.initServer();
            RpcServerTemplate serverTemplate = RpcFactory.createServerTemplate(weakCachingResolver, true);

            serverTemplate.bind(10000, "123", (throwable, receiver) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    receiver.receive("test", (Listener<Long>) (time, channel) -> {
                        channel.response(time, (throwable2, object) -> {
                            if (throwable2 != null) throwable2.printStackTrace();
                        });
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
