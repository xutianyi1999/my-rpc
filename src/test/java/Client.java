import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.channel.Sender;
import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.template.RpcClientTemplate;
import io.netty.channel.ChannelFuture;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        RpcFactory.clientInit();
        RpcClientTemplate clientTemplate = RpcFactory.createClientTemplate(MyResponseMessage.class);
        Sender connect = clientTemplate.connect("127.0.0.1", 1999);
        MyRequestMessage myRequestMessage = new MyRequestMessage();
        myRequestMessage.setTime(System.currentTimeMillis());
        connect.send(myRequestMessage, new Callback<MyResponseMessage>() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    future.cause().printStackTrace();
                }
            }

            @Override
            public void response(MyResponseMessage myResponseMessage) {
                String content = myResponseMessage.getContent();
                System.out.println(content);
            }
        });
    }
}
