import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.template.RpcClientTemplate;

public class Client {

    public static void main(String[] args) {
        try {
            RpcFactory.initClient();
            RpcClientTemplate clientTemplate = RpcFactory.createClientTemplate();

            clientTemplate.connect("127.0.0.1", 10000, (throwable, sender) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    sender.addListenerInactive(remoteAddress -> System.out.println(remoteAddress + " disconnect"));

                    sender.send(System.currentTimeMillis(), (throwable2, object) -> {
                        if (throwable2 != null) {
                            throwable2.printStackTrace();
                        }
                    }, (Callback<Long>) time -> System.out.println(System.currentTimeMillis() - time));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
