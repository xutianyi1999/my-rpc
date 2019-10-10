# my-rpc-core
基于netty的异步rpc

## Usages
### Client
```java
import club.koumakan.rpc.core.ClassResolverType;
import club.koumakan.rpc.core.RpcFactory;
import club.koumakan.rpc.core.client.ConnectConfig;
import club.koumakan.rpc.core.client.functional.Callback;
import club.koumakan.rpc.core.template.RpcClientTemplate;

public class Client {

    public static void main(String[] args) {
        try {
            RpcFactory.initClient();
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
```
### Server
```java
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
```