import club.koumakan.rpc.ClassResolverType;
import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.client.ConnectConfig;
import club.koumakan.rpc.client.functional.Callback;
import club.koumakan.rpc.template.RpcClientTemplate;

public class Client {

    public static void main(String[] args) {
        try {
            RpcFactory.initClient();
            RpcClientTemplate clientTemplate = RpcFactory.createClientTemplate(ClassResolverType.weakCachingResolver, true);

            clientTemplate.connect(new ConnectConfig("127.0.0.1", 19999, "123", -1, 1000), (throwable, sender) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    sender.addListenerInactive(System.out::println);

                    sender.send("test", System.currentTimeMillis(), (throwable1, object) -> {
                        if (throwable1 != null) throwable1.printStackTrace();
                    }, (Callback<Long>) responseMessage -> {
                        System.out.println(System.currentTimeMillis() - responseMessage);
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
