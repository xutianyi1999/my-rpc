import club.koumakan.rpc.core.RpcConfig;
import club.koumakan.rpc.core.RpcCore;
import club.koumakan.rpc.core.RpcFactory;
import club.koumakan.rpc.core.client.ConnectConfig;
import club.koumakan.rpc.core.client.functional.Callback;
import club.koumakan.rpc.core.exception.RpcCoreException;

public class Client {

    public static void main(String[] args) throws RpcCoreException {
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
                        sender.addInactiveListener(remoteAddress -> {
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
