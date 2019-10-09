import club.koumakan.rpc.ClassResolverType;
import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.client.ConnectConfig;
import club.koumakan.rpc.client.functional.Callback;
import club.koumakan.rpc.template.RpcClientTemplate;

public class Client {

    public static void main(String[] args) {
        try {
            RpcFactory.initClient();
            RpcFactory.setCallbackTimeout(1000);
            RpcClientTemplate clientTemplate = RpcFactory.createClientTemplate(ClassResolverType.softCachingConcurrentResolver, true, true, false);

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
                                        Thread.sleep(1000);
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
