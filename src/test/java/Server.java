import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.server.Listener;
import club.koumakan.rpc.template.RpcServerTemplate;

public class Server {

    public static void main(String[] args) {
        try {
            RpcFactory.initServer();
            RpcServerTemplate serverTemplate = RpcFactory.createServerTemplate();

            serverTemplate.bind(10000, (throwable, receiver) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    receiver.receive((Listener<Long>) (time, channel) -> channel.response(time, (throwable2, object) -> {
                        if (throwable2 == null) {
                            System.out.println("SUCCESS");
                        } else {
                            throwable2.printStackTrace();
                        }
                    }));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
