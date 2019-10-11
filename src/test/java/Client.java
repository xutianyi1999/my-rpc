import club.koumakan.rpc.core.ClassResolverType;
import club.koumakan.rpc.core.RpcFactory;
import club.koumakan.rpc.core.client.ConnectConfig;
import club.koumakan.rpc.core.client.RpcClient;
import club.koumakan.rpc.core.client.functional.Callback;

public class Client {

    public static void main(String[] args) {
        try {
            RpcFactory.initClient();
            RpcClient clientTemplate = RpcFactory.createClientTemplate(ClassResolverType.softCachingConcurrentResolver, true, true, false);

            clientTemplate.connect(new ConnectConfig("127.0.0.1", 19999, "123", -1, 1000),
                    (throwable, sender) -> {
                        if (throwable != null) {
                            throwable.printStackTrace();
                        } else {
                            sender.addListenerInactive(System.out::println);

                            new Thread(() -> {
                                while (true) {
                                    sender.send("test",
                                            System.currentTimeMillis(), (Callback<Long>) (throwable2, responseMessage) -> {
                                                if (throwable2 != null) {
                                                    throwable2.printStackTrace();
                                                } else {
                                                    System.out.println(System.currentTimeMillis() - responseMessage);
                                                }
                                            });

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
