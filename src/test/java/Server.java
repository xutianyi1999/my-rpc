import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.channel.Receiver;
import club.koumakan.rpc.server.Listener;
import club.koumakan.rpc.template.RpcServerTemplate;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        RpcFactory.serverInit();
        RpcServerTemplate serverTemplate = RpcFactory.createServerTemplate(MyRequestMessage.class);
        Receiver receiver = serverTemplate.bind(1999);
        receiver.receive((Listener<MyRequestMessage>) (myRequestMessage, channel) -> {
            long l = System.currentTimeMillis();
            System.out.println(l - myRequestMessage.getTime());
            MyResponseMessage myResponseMessage = new MyResponseMessage(myRequestMessage.getCallId());
            myResponseMessage.setContent("test");
            channel.response(myResponseMessage, new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Throwable cause = future.cause();
                    if (cause != null) {
                        cause.printStackTrace();
                    }
                }
            });
        });
    }
}
