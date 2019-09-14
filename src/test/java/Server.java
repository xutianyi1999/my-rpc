import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.channel.Receiver;
import club.koumakan.rpc.server.Listener;
import club.koumakan.rpc.template.RpcServerTemplate;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        RpcFactory.serverInit();
        RpcServerTemplate serverTemplate = RpcFactory.createServerTemplate(MyRequestMessage.class);
        Receiver receiver = serverTemplate.bind(1999);
        receiver.receive((Listener<MyRequestMessage>) (myRequestMessage, channel) -> {
            long l = System.currentTimeMillis();
            System.out.println(l - myRequestMessage.getTime());
        });
    }
}
