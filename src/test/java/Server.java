import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.channel.Receiver;
import club.koumakan.rpc.server.Listener;
import club.koumakan.rpc.template.RpcServerTemplate;
import io.netty.channel.ChannelFutureListener;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        RpcFactory.serverInit();
        RpcServerTemplate serverTemplate = RpcFactory.createServerTemplate();
        Receiver receiver = serverTemplate.bind(1999);
        receiver.receive((Listener<MyRequestMessage>) (requestMessage, channel) -> {
            long time1 = requestMessage.getTime();
            long time2 = System.currentTimeMillis();
            System.out.println(time2 - time1);

            MyResponseMessage myResponseMessage = new MyResponseMessage();
            myResponseMessage.setContent("test");
            channel.response(myResponseMessage, (ChannelFutureListener) future -> {
                Throwable cause = future.cause();

                if (cause != null) {
                    cause.printStackTrace();
                }
            });
        });
    }
}
