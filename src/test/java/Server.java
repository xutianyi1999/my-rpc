import club.koumakan.rpc.RpcFactory;

public class Server {

    public static void main(String[] args) {
        try {
            RpcFactory.initServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
