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
                    receiver.receive("test", (Listener<MyRequestMessage>) (requestMessage, channel) -> {
                        System.out.println(requestMessage.getTime());

                        channel.response("test", (throwable2, object) -> {
                            if (throwable2 != null) throwable2.printStackTrace();
                        });
                    });

                    receiver.receive("test2", (Listener<String>) (requestMessage, channel) -> {
                        System.out.println(requestMessage);

                        channel.response("test2", (throwable3, object) -> {
                            if (throwable3 != null) throwable3.printStackTrace();
                        });
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
