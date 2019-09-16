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
                        sender.send(System.currentTimeMillis(), new Callback<Long>() {
                            @Override
                            public void operationComplete(boolean isSuccess, Throwable throwable) {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                            }

                            @Override
                            public void response(Long responseMessage) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(System.currentTimeMillis() - responseMessage);
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
