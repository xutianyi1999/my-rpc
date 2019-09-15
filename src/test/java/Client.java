import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.channel.Sender;
import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.template.RpcClientTemplate;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        RpcFactory.clientInit();
        RpcClientTemplate clientTemplate = RpcFactory.createClientTemplate();
        Sender connect = clientTemplate.connect("127.0.0.1", 1999);
        MyRequestMessage myRequestMessage = new MyRequestMessage();
        myRequestMessage.setTime(System.currentTimeMillis());
        connect.send(myRequestMessage, new Callback<MyResponseMessage>() {

            @Override
            public void operationComplete(boolean isSuccess, Throwable throwable) {
                if (throwable != null) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void response(MyResponseMessage responseMessage) {
                System.out.println(responseMessage.getContent());
            }
        });
    }
}
