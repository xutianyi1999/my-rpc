import club.koumakan.rpc.Future;
import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.template.RpcClientTemplate;

public class Client {

    public static void main(String[] args) {
        try {
            RpcFactory.initClient();
            RpcClientTemplate clientTemplate = RpcFactory.createClientTemplate();
            clientTemplate.connect("127.0.0.1", 10, (throwable, sender) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    MyRequestMessage myRequestMessage = new MyRequestMessage();
                    myRequestMessage.setTime(System.currentTimeMillis());
                    sender.send(myRequestMessage, (Future) (throwable2, object) -> {

                    }, (Callback<String>) object -> {

                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
