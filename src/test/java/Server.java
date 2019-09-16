import club.koumakan.rpc.Future;
import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.channel.Receiver;
import club.koumakan.rpc.server.Listener;
import club.koumakan.rpc.template.RpcServerTemplate;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        try {
            RpcFactory.serverInit();
            RpcServerTemplate serverTemplate = RpcFactory.createServerTemplate();
            Receiver receiver = serverTemplate.bind(1999);
            receiver.receive((Listener<MyRequestMessage>) (requestMessage, channel) -> {
                long time1 = requestMessage.getTime();
                long time2 = System.currentTimeMillis();
                System.out.println(time2 - time1);

                MyResponseMessage myResponseMessage = new MyResponseMessage();
                myResponseMessage.setContent("test");
                channel.response(myResponseMessage, new Future() {
                    @Override
                    public void operationComplete(boolean isSuccess, Throwable throwable) {
                        if (throwable != null) {
                            throwable.printStackTrace();
                        }
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
