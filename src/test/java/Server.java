import club.koumakan.rpc.Future;
import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.channel.Receiver;
import club.koumakan.rpc.server.BindFuture;
import club.koumakan.rpc.server.Listener;
import club.koumakan.rpc.template.RpcServerTemplate;

public class Server {

    public static void main(String[] args) {
        try {
            RpcFactory.initServer();
            RpcServerTemplate serverTemplate = RpcFactory.createServerTemplate();
            serverTemplate.bind(1999, new BindFuture() {

                @Override
                public void operationComplete(boolean isSuccess, Throwable throwable, Receiver receiver) {
                    if (isSuccess && throwable == null) {
                        receiver.receive((Listener<String>) (str, channel) -> {
                            System.out.println(str);
                            channel.response("server", new Future() {
                                @Override
                                public void operationComplete(boolean isSuccess, Throwable throwable) {
                                    if (throwable != null) {
                                        throwable.printStackTrace();
                                    }
                                }
                            });
                        });
                    } else {
                        throwable.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
