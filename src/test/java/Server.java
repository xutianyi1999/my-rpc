import club.koumakan.rpc.core.RpcConfig;
import club.koumakan.rpc.core.RpcCore;
import club.koumakan.rpc.core.RpcFactory;
import club.koumakan.rpc.core.exception.ResponseException;

public class Server {

    public static void main(String[] args) {
        RpcFactory.createRpcServer(RpcCore.server(), new RpcConfig().setKey("123").setCompression(true))
                .bind(19999, (throwable, receiver) -> {
                    receiver.publish("test", (requestMessage, channel) -> {
                        try {
                            channel.response(requestMessage);
                        } catch (ResponseException e) {
                            e.printStackTrace();
                        }
                    });
                });
    }
}
