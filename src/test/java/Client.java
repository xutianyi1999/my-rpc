import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.template.RpcClientTemplate;

import static club.koumakan.rpc.ClassResolverType.weakCachingResolver;

public class Client {

    public static void main(String[] args) {
        try {
            RpcFactory.initClient();
            RpcClientTemplate clientTemplate = RpcFactory.createClientTemplate(weakCachingResolver, true);

            clientTemplate.connect("127.0.0.1", 10000, "123", (throwable, sender) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    sender.addListenerInactive((remoteAddress, reconnectionHandler) -> {
                        System.out.println(remoteAddress);

                        reconnectionHandler.add(clientTemplate, reSender -> {
                            System.out.println(reSender.isActive());
                        });
                    });

                    sender.send("test", System.currentTimeMillis(), (throwable2, object) -> {
                        if (throwable2 != null) throwable2.printStackTrace();
                    }, (Callback<Long>) (time) -> {
                        System.out.println(System.currentTimeMillis() - time);
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
