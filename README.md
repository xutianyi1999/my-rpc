# my-rpc-core
基于netty的异步rpc

## Usages
### Client
```java
import club.koumakan.rpc.core.RpcConfig;
import club.koumakan.rpc.core.RpcCore;
import club.koumakan.rpc.core.RpcFactory;
import club.koumakan.rpc.core.client.ConnectConfig;
import club.koumakan.rpc.core.client.functional.Callback;

public class Client {

    public static void main(String[] args) {
        RpcFactory.createRpcClient(
                RpcCore.client(),
                new RpcConfig()
                        .setClassResolverType(RpcFactory.ClassResolverType.softCachingConcurrentResolver)
                        .setCompression(true)
                        .setKey("123")
                        .setNoDelay(false)
        ).connect(
                new ConnectConfig()
                        .setIpAddress("127.0.0.1")
                        .setPort(19999)
                        .setRetries(-1)
                        .setSleepMs(5000),
                (throwable, sender) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else {
                        sender.addListenerInactive(remoteAddress -> {
                            System.out.println(remoteAddress.getHostString());
                        });
                        new Thread(() -> {
                            while (true) {
                                sender.send("test", System.currentTimeMillis(), (Callback<Long>) (throwable2, responseMessage) -> {
                                    if (throwable2 != null) {
                                        throwable2.printStackTrace();
                                    } else {
                                        System.out.println(System.currentTimeMillis() - responseMessage);
                                    }
                                }, 1000);
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
        );
    }
}
```
### Server
```java
import club.koumakan.rpc.core.RpcConfig;
import club.koumakan.rpc.core.RpcCore;
import club.koumakan.rpc.core.RpcFactory;
import club.koumakan.rpc.core.exception.ResponseException;
import club.koumakan.rpc.core.server.functional.Listener;

public class Server {

    public static void main(String[] args) {
        RpcFactory.createRpcServer(RpcCore.server(), new RpcConfig().setKey("123").setCompression(true))
                .bind(19999, (throwable, receiver) -> {
                    receiver.publish("test", (Listener<Long>) (requestMessage, channel) -> {
                        try {
                            channel.response(requestMessage);
                        } catch (ResponseException e) {
                            e.printStackTrace();
                        }
                    });
                });
    }
}
```