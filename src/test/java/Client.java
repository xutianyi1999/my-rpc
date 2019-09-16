import club.koumakan.rpc.RpcFactory;
import club.koumakan.rpc.channel.Sender;
import club.koumakan.rpc.client.Callback;
import club.koumakan.rpc.client.ConnectFuture;
import club.koumakan.rpc.template.RpcClientTemplate;

public class Client {

    public static void main(String[] args) {
        try {
            RpcFactory.initClient();
            RpcClientTemplate clientTemplate = RpcFactory.createClientTemplate();
            clientTemplate.connect("127.0.0.1", 1999, new ConnectFuture() {
                @Override
                public void operationComplete(boolean isSuccess, Throwable throwable, Sender sender) {
                    if (isSuccess && throwable == null) {
                        String str = "client";
                        sender.send(str, new Callback<String>() {
                            @Override
                            public void operationComplete(boolean isSuccess, Throwable throwable) {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                            }

                            @Override
                            public void response(String responseMessage) {
                                System.out.println(responseMessage);
                            }
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
